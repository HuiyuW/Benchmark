import os
import time
import gc
from datetime import datetime
import torch
import httpx
from transformers import AutoTokenizer, AutoModelForCausalLM, pipeline
from langchain import LLMChain, PromptTemplate
from langchain_ollama import ChatOllama
from openai import OpenAI
import json
import re

# 设定 Ollama 服务器地址（如有）
os.environ["OLLAMA_HOST"] = "http://172.17.61.133:11434"

# VW LLM API 配置
VW_CLIENT_ID = "idp-99629305-eb32-4450-af6d-0762ac02ca2b-llmaas-app"
VW_CLIENT_SECRET = "cidps_YUy05kngKz8GhSMFp4ghXHsHbiZt2h9BQsb6K4pP8zlTdF1Q9D8rTeHgbCFKmQJ5USqO541ogcf7cdHxDFTh5EQr2DCcz9"
VW_LLM_API_CLIENT_ID = "ESE-rs6yvgabK2X9vhd1U69"
VW_BASE_URL = "https://llm.ai.vwgroup.com/v1"

def clean_java_code(result: str) -> str:
    """
    自动清理生成的 Java 代码：
    1. 去掉开头的 ```java 及其之前的所有内容。
    2. 去掉结尾的 ``` 及其之后的所有内容。
    
    参数：
        result (str): Deepseek Coder 生成的原始文本。
        
    返回：
        str: 清理后的 Java 代码。
    """
    # 检测并去掉所有 ` ```java ` 之前的内容
    java_start_pattern = r"```java\s*"
    if re.search(java_start_pattern, result):
        result = re.split(java_start_pattern, result, maxsplit=1)[1]

    # 检测并去掉所有 ` ``` ` 之后的内容
    java_end_pattern = r"\s*```"
    if re.search(java_end_pattern, result):
        result = re.split(java_end_pattern, result, maxsplit=1)[0]
    
    # 去除多余的空白行
    result = result.strip()
    return result


def indent_text(text, spaces=4):
    """
    Indent each line of the given text by a specified number of spaces.

    Args:
        text (str): The text to be indented.
        spaces (int): Number of spaces to use for indentation.

    Returns:
        str: Indented text.
    """
    indentation = " " * spaces
    return "\n".join(f"{indentation}{line}" for line in text.splitlines())


def strip_prompt(output, marker):
    """
    Remove everything before and including the marker from the output.
    
    Args:
        output (str): The generated output text.
        marker (str): The marker string to look for in the output.

    Returns:
        str: The portion of the output after the marker, or the original output if the marker is not found.
    """
    if marker in output:
        return output.split(marker, 1)[1].strip()
    return output


class LLMSelector:
    def __init__(self, model_type="huggingface", model_name_or_path=None):
        self.model_type = model_type
        self.model_name_or_path = model_name_or_path
        self.llm = self.initialize_llm()

    def initialize_llm(self):
        if self.model_type == "vw_llm":
            return self._initialize_vw_llm()
        elif self.model_type == "ollama":
            return self._initialize_ollama()
        elif self.model_type == "huggingface":
            return self._initialize_huggingface()
        else:
            raise ValueError(f"不支持的 LLM 类型: {self.model_type}")

    def _initialize_vw_llm(self):
        """ 初始化 VW Group LLM """
        token = self._get_vw_token()
        return OpenAI(
            api_key=token,
            base_url=VW_BASE_URL,
            default_headers={"X-LLM-API-CLIENT-ID": VW_LLM_API_CLIENT_ID}
        )

    def _initialize_ollama(self):
        """ 初始化本地 Ollama 模型 """
        return ChatOllama(model=self.model_name_or_path, num_ctx=6000, temperature=0.0)

    def _initialize_huggingface(self):
        """ 加载 Hugging Face 本地 Transformer 模型 """
        tokenizer = AutoTokenizer.from_pretrained(self.model_name_or_path)
        model = AutoModelForCausalLM.from_pretrained(self.model_name_or_path, torch_dtype=torch.float16, device_map="auto")
        pipe = pipeline(
            "text-generation",
            model=model,
            tokenizer=tokenizer,
            max_length=4096,
            do_sample=False,
            top_p=1,
            repetition_penalty=1.15
        )
        return pipe

    def _get_vw_token(self):
        """ 获取 VW Cloud IDP 认证 Token """
        url = "https://idp.cloud.vwgroup.com/auth/realms/kums-mfa/protocol/openid-connect/token"
        response = httpx.post(
            url,
            data={
                "client_id": VW_CLIENT_ID,
                "client_secret": VW_CLIENT_SECRET,
                "grant_type": "client_credentials",
            },
            headers={"Content-Type": "application/x-www-form-urlencoded"},
            timeout=10
        )
        if response.status_code == 200:
            return response.json()["access_token"]
        else:
            raise Exception(f"获取 VW Token 失败: {response.status_code} - {response.text}")

    def generate_response(self, template_text: str, user_task: str):
        """ 根据选择的 LLM 生成响应 """
        format_prompt = template_text.format(task=user_task)
        if self.model_type == "vw_llm":
            response = self.llm.chat.completions.create(
                model="gpt-4o",
                messages=[{"role": "user", "content": format_prompt}],
                max_tokens=2048,
                stream=False,
                temperature=0.0
            )
            return response.choices[0].message.content.strip()

        elif self.model_type == "ollama":
            chain = LLMChain(llm=self.llm, prompt=PromptTemplate(input_variables=["task"], template=template_text))
            return chain.run(task=user_task)
        elif self.model_type == "huggingface":
            response = self.llm(format_prompt)
            return response[0]['generated_text'].strip()
        else:
            raise ValueError(f"不支持的 LLM 类型: {self.model_type}")
def sanitize_path(path: str) -> str:
    """去掉路径中的非法字符"""
    return re.sub(r'[:/\\]', '_', path)

def generate_step_definitions(task_gherkin, model_type="huggingface", model_name_or_path=None, saved_step_path=None, filename = None, prompt_template = None, log_directory = None, add_java_header = True):
    device = torch.device("cuda:0")
    
    # 选择 LLM
    llm_selector = LLMSelector(model_type, model_name_or_path)
 
    # 生成 Prompt
    
    start_time = time.time()
    # 生成响应
    result = llm_selector.generate_response(prompt_template, task_gherkin)
    marker = "<|eot_id|><|start_header_id|>assistant<|end_header_id|>"
    result_without_prompt = strip_prompt(result, marker)
    print(result_without_prompt)
    if result_without_prompt.startswith(" ```java"):
        result_without_prompt = result_without_prompt[8:].strip()
        if result_without_prompt.endswith("```"):
            result_without_prompt = result_without_prompt[:-3].strip()
    # result = llm_selector.generate_response(template(task=task_gherkin))
    end_time = time.time()
    if model_type == "vw_llm":
        token_count = len(result.split())  # 近似估计 VW LLM 生成的 token 数
    elif model_type == "ollama":
        token_count = len(result.split())  # Ollama 没有直接 token 统计
    elif model_type == "huggingface":
        tokenizer = AutoTokenizer.from_pretrained(model_name_or_path)
        token_count = len(tokenizer.encode(result))  # Hugging Face 使用 tokenizer 计算 token 数
    else:
        token_count = -1  # 未知情况
    elapsed_time = end_time - start_time  # 计算时间（秒）
    tokens_per_second = token_count / elapsed_time if elapsed_time > 0 else 0
    # 记录日志
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    log_file = f"step_definition_{timestamp}_{model_type}_{model_name_or_path}.txt"
    log_file = sanitize_path(log_file)
    if log_directory:
        if not os.path.exists(log_directory):
            os.makedirs(log_directory)
        time_model_str = f'{timestamp}_{model_type}_{model_name_or_path}'
        time_model_str = sanitize_path(time_model_str)
        log_directory = f'{log_directory}/{time_model_str}' 
        os.makedirs(log_directory)
        log_file = os.path.join(log_directory, log_file)

    with open(log_file, "w", encoding="utf-8") as log:
        log.write(f"Model Type: {model_type}\n")
        log.write(f"Model Path/Name: {model_name_or_path}\n")
        log.write(f"Device: {device}\n")
        log.write(f"Total Tokens Generated: {token_count}\n")
        log.write(f"Elapsed Time: {elapsed_time:.2f} seconds\n")
        log.write(f"Tokens per Second: {tokens_per_second:.2f}\n")
        log.write(f"Output:\n{result}\n")
    # if model_type=="ollama":

    #     del chain
    gc.collect()
    torch.cuda.empty_cache()

    print("All experiments completed.")

    java_header = f"""
package huiyu.stepdefinitions_{filename};

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import huiyu.BaseTest;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;

public class {filename} extends BaseTest {{

    private WebDriverWait wait;

    @Before
    public void init() {{
        setUp();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }}
"""

    
    with open(saved_step_path, "w", encoding="utf-8") as f:
        # 写入 Java 文件头部
        if add_java_header:
            f.write(java_header.strip() + "\n\n")
            # 写入缩进的 result_without_prompt
            f.write(indent_text(clean_java_code(result_without_prompt)) + "\n")
            # 写入 Java 文件结尾的 "}"
            f.write("\n}\n")  # 第一个关闭类的 "}"
        else:
            f.write(clean_java_code(result_without_prompt))
    return log_directory
