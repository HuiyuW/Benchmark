import os
import time
import re
import json
# from transformers import AutoTokenizer, LlamaForCausalLM, pipeline, AutoModelForCausalLM
from langchain import LLMChain, HuggingFacePipeline, PromptTemplate
from langchain_ollama import ChatOllama
import torch
from datetime import datetime
import gc
os.environ["OLLAMA_HOST"] = "http://172.17.61.133:11434"


device = torch.device("cuda:0")
template = '''
You are an intelligent assistant. Your task is to produce a structured JSON object that maps Gherkin scenario steps to the corresponding DOM elements. The JSON must have the following structure:

1. The root of the JSON is an object.
2. Each key in this object corresponds to a full Gherkin step (e.g., “Given I open the login page ...”).
3. Each value is another object that includes only the relevant DOM element(s). Use a key that corresponds to the descriptive name (e.g., “username”, “password”, “sign_button”, “login_page”, “successful_login”, etc.), and map it to an object containing “css_selector” and “xpath” (optionally “matched_text” as well).
4. If a Gherkin step does not involve any DOM elements, the step’s value should still be present as an object, but with empty “css_selector” and “xpath”.
5. Only include elements that actually match the Gherkin steps. Do not add unrelated or duplicate elements.
6. Return *only* valid JSON, with no additional commentary, explanation, or text outside of JSON braces.

Here is your Gherkin content:
{gherkin}

Here are your DOM elements:
{dom_elements}

Please strictly follow the above instructions and output only a well-formed JSON object. No extra text.
'''


prompt = PromptTemplate(
    input_variables=["gherkin", "dom_elements"],
    template=template
)

gherkin_text = """Feature: Login to the Merchant Admin Portal

  Scenario: Successful login to the Merchant Admin Portal
    Given I open the login page "http://localhost:7780/admin"
    Then I enter username "newadmin" and password "NewAdmin123"
    And I click the sign button
    Then I should be logged in
"""

dom_elements = '''

    {
  "steps": [
    {
      "step_number": 1,
      "goal": "Navigate to the login page",
      "actions": [
        {
          "type": "open_tab",
          "details": {
            "url": "http://localhost:7780/admin"
          }
        }
      ],
      "result": [
        "🔗  Opened new tab with http://localhost:7780/admin"
      ]
    },
    {
      "step_number": 2,
      "goal": "Enter username, then password, then click Sign In",
      "actions": [
        {
          "type": "input_text",
          "details": {
            "index": 3,
            "text": "newadmin"
          },
          "element": {
            "xpath": "html/body/section/div/form/fieldset/div/div/input",
            "css_selector": "html > body > section > div > form > fieldset > div > div > input.admin__control-text[id=\"username\"][type=\"text\"][name=\"login[username]\"][placeholder=\"user name\"][autocomplete=\"off\"]",
            "outer_html": "<input id=\"username\" class=\"admin__control-text\" type=\"text\" name=\"login[username]\" autofocus=\"\" value=\"\" data-validate=\"{required:true}\" placeholder=\"user name\" autocomplete=\"off\" style=\"\" browser-user-highlight-id=\"playwright-highlight-3\">"
          }
        },
        {
          "type": "input_text",
          "details": {
            "index": 5,
            "text": "NewAdmin123"
          },
          "element": {
            "xpath": "html/body/section/div/form/fieldset/div[2]/div/input",
            "css_selector": "html > body > section > div > form > fieldset > div:nth-of-type(2) > div > input.admin__control-text[id=\"login\"][type=\"password\"][name=\"login[password]\"][placeholder=\"password\"][autocomplete=\"off\"]",
            "outer_html": "<input id=\"login\" class=\"admin__control-text\" type=\"password\" name=\"login[password]\" data-validate=\"{required:true}\" value=\"\" placeholder=\"password\" autocomplete=\"off\" style=\"\" browser-user-highlight-id=\"playwright-highlight-5\">"
          }
        },
        {
          "type": "click_element",
          "details": {
            "index": 6
          },
          "element": {
            "xpath": "html/body/section/div/form/fieldset/div[3]/div/button",
            "css_selector": "html > body > section > div > form > fieldset > div:nth-of-type(3) > div > button.action-login.action-primary",
            "outer_html": "<button class=\"action-login action-primary\" browser-user-highlight-id=\"playwright-highlight-6\">\n        <span>Sign in</span>\n    </button>"
          }
        }
      ],
      "result": [
        "⌨️  Input newadmin into index 3",
        "⌨️  Input NewAdmin123 into index 5",
        "🖱️  Clicked button with index 6: Sign in"
      ]
    },
    {
      "step_number": 3,
      "goal": "No further actions needed as all tasks are completed.",
      "actions": [
        {
          "type": "done",
          "details": {
            "text": "Successfully logged in to the Merchant Admin Portal as newadmin."
          }
        }
      ],
      "result": [
        "Successfully logged in to the Merchant Admin Portal as newadmin."
      ]
    }
  ]
}

'''


llm = ChatOllama(
    # model="deepseek-r1:32b",
    # model="deepseek-coder-v2:16b",
    # model="qwen2.5:14b",
    model="deepseek-coder:6.7b",
    num_ctx=6000,
    temperature=0.0,
    top_k=1,             # <- 控制选择前K个词
    top_p=1.0  
)
chain = LLMChain(llm=llm, prompt=prompt,)
result = chain.run({
    "gherkin": gherkin_text,
    "dom_elements": dom_elements
})
print(result)

# 利用正则表达式提取完整的 JSON（从第一个 "{" 到最后一个 "}"）
match = re.search(r'(\{.*\})', result, re.DOTALL)
if not match:
    raise ValueError("No valid JSON found in the model's output.")

json_str = match.group(1)

# 解析为 Python 对象
try:
    parsed_data = json.loads(json_str)
except json.JSONDecodeError as e:
    raise ValueError(f"Failed to parse JSON: {e}")

# 将 JSON 数据保存到文件 output.json
with open("output.json", "w", encoding="utf-8") as f:
    json.dump(parsed_data, f, ensure_ascii=False, indent=2)

print("Successfully extracted and saved JSON to output.json.")