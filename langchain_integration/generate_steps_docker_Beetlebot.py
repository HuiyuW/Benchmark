import os
import time
from transformers import AutoTokenizer, LlamaForCausalLM, pipeline, AutoModelForCausalLM
from langchain import LLMChain, HuggingFacePipeline, PromptTemplate
import torch
from datetime import datetime
import gc

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

def generate_step_definitions(task_gherkin):
    model_paths = [
        # "C:/Huiyu_Wang/Work/code/LLM/Llama-3.1-8B-Instruct",
        # "C:/Huiyu_Wang/Work/code/LLM/starcoder2-3b"
        "C:/Huiyu_Wang/Work/code/LLM/deepseek-coder-6.7b-instruct"
    ]

    device = torch.device("cuda:0")

    template = '''
<|begin_of_text|><|start_header_id|>system<|end_header_id|>

Cutting Knowledge Date: December 2023
Today Date: Jan 19, 2025

You are a helpful assistant that can read a Gherkin Feature file and produce Java-based Selenium Step Definitions. You will be given:

1. An **example Gherkin Feature file** and **its corresponding Step Definition** as references, so you know how the final result should look.
2. A list of **element locators** (CSS or XPath) used in the Gherkin Feature file so you can create the appropriate selectors.
3. A **pinned portion** of the Step Definition code (e.g., dependencies, imports, @Before annotations), which must remain intact. You must only generate the remaining part of the code.

Your tasks:

- For each scenario/step in the user's Gherkin Feature file, provide a **fully implemented** Step Definition method in Java.
- Ensure the code is **ready to run** in Selenium without requiring placeholders or JavaScript.
- **Use Selenium’s native methods** for actions such as scrolling or clicking buttons; do **not** rely on custom JavaScript execution.
- **Use the provided locators** (CSS or XPath) exactly as shown below when identifying elements.
- Keep the pinned code sections exactly as given and **only** produce the additional code needed to complete the Step Definition.
- Avoid empty or placeholder implementations; fill in as much detail as possible.
- Final output should be **directly usable** in a typical Selenium/JUnit (or TestNG) project.

<|eot_id|><|start_header_id|>user<|end_header_id|>

Below is my input. Please read all parts, then generate the requested Step Definition code (excluding the pinned section, which is already provided):


**1. Relevant Element Locators**  

- **Username Input**: `By.cssSelector("groupui-input[id='welcome-user-name']")`
- **APIKey Input**: `By.cssSelector("groupui-input[id='welcome-api-key']")`
- **Login Button**: `By.cssSelector("#welcome-login")`
- **Terms & Conditions Headline**: `By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]")`
- **Accept Button**: `By.cssSelector("groupui-button#welcome-agree-agb")`
- **Profile Header**: `By.cssSelector("#app-profile-header")`
- **Profile Avatar**: `By.cssSelector("groupui-avatar")`
- **Toast Title**: `By.cssSelector(".toast-title")`
- **Logout Button**: `By.cssSelector("#app-logout")`


**2. Pinned Step Definition Code (Imports, Dependencies, @Before, etc.)**  

package huiyu.stepdefinitions_login_test_Beetlebot;

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

public class Login_test_Beetlebot extends BaseTest {{

    private WebDriverWait wait;

    @Before
    public void init() {{
        setUp();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }}

**3. Actual Gherkin Feature File to Implement**  

{task}


Please generate the **remaining** Java Selenium Step Definitions to fully cover Actual Gherkin Feature File, using the pinned portion as is.

<|eot_id|><|start_header_id|>assistant<|end_header_id|>
'''

    prompt = PromptTemplate(
        input_variables=["task"],
        template=template
    )

    for model_path in model_paths:
        model_name = os.path.basename(model_path)
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        log_file = f"step_definition_{timestamp}_{model_name}.txt"

        with open(log_file, "w", encoding="utf-8") as log:
            log.write(f"Model Path: {model_path}\n")
            log.write(f"Device: {device}\n")
            log.write("Parameters: max_length=4096, top_p=1, repetition_penalty=1.15\n\n")

            tokenizer = AutoTokenizer.from_pretrained(model_path)
            model = AutoModelForCausalLM.from_pretrained(model_path, torch_dtype=torch.float16, device_map=device)
            pipe = pipeline(
                "text-generation",
                model=model,
                tokenizer=tokenizer,
                max_length=4096,
                top_p=1,
                repetition_penalty=1.15
            )
            llama_model = HuggingFacePipeline(pipeline=pipe)
            chain = LLMChain(llm=llama_model, prompt=prompt)

            start_time = time.time()
            log.write(f"Experiment Start Time: {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(start_time))}\n\n")

            total_tokens = 0

            for i in range(1, 2):
                log.write(f"--- Test Run {i} ---\n")
                print(f"--- Test Run {i} ---\n")
                test_start = time.time()

                result = chain.run(task=task_gherkin)
                marker = "<|eot_id|><|start_header_id|>assistant<|end_header_id|>"
                result_without_prompt = strip_prompt(result, marker)
                test_end = time.time()

                num_tokens = len(tokenizer.encode(result_without_prompt))
                total_tokens += num_tokens
                tokens_per_second = num_tokens / (test_end - test_start)

                log.write(f"Output (With Prompt):\n{result}\n")
                print(f"Output (Without Prompt):\n{result_without_prompt}\n")
                log.write(f"Test Run {i} Duration: {test_end - test_start:.2f} seconds\n")
                log.write(f"Generated Tokens: {num_tokens}\n")
                log.write(f"Generation Speed: {tokens_per_second:.2f} tokens/sec\n\n")
                print(f"Test Run {i} Duration: {test_end - test_start:.2f} seconds\n")
                print(f"Generated Tokens: {num_tokens}")
                print(f"Generation Speed: {tokens_per_second:.2f} tokens/sec\n")

            end_time = time.time()
            log.write(f"Experiment End Time: {time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(end_time))}\n")
            log.write(f"Total Duration: {end_time - start_time:.2f} seconds\n")
            log.write(f"Total Generated Tokens: {total_tokens}\n")
            print(f"Total Generated Tokens: {total_tokens}\n")

        del chain, llama_model, pipe, model, tokenizer
        gc.collect()
        torch.cuda.empty_cache()

    print("All experiments completed.")

    java_header = """
package huiyu.stepdefinitions_login_test_Beetlebot;

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

public class Login_test_Beetlebot extends BaseTest {

    private WebDriverWait wait;

    @Before
    public void init() {
        setUp();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
    """

    steps_path = "src/test/java/huiyu/stepdefinitions_login_test_Beetlebot/Login_test_Beetlebot.java"
    with open(steps_path, "w", encoding="utf-8") as f:
        # 写入 Java 文件头部
        f.write(java_header.strip() + "\n\n")
        # 写入缩进的 result_without_prompt
        f.write(indent_text(result_without_prompt) + "\n")
        # 写入 Java 文件结尾的 "}"
        f.write("\n}\n")  # 第一个关闭类的 "}"
    return steps_path
