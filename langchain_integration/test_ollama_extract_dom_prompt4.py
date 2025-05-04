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
You are an intelligent assistant. Your task is to produce a structured JSON object that maps each Gherkin scenario step to the corresponding DOM elements. Follow these **strict** requirements:

1. The **root** of your JSON is an object.
2. Each **key** in this root object is the **exact Gherkin step** string (e.g., "Given I open the login page ...").
3. Each step’s **value** is another object; inside it, for each relevant DOM element, use a **descriptive key** (e.g. "username", "password", "sign_button", etc.).
4. For each such DOM element, you must include **exactly** the following fields:
   - "css_selector": must be verbatim from the "dom_elements" data
   - "xpath": must be verbatim from the "dom_elements" data
   - "outer_html": must be verbatim from the "dom_elements" data
   - "page_title": page_title of the current DOM element
   - "matched_text" can be included if you find it helpful, but it is optional.
5. **Do not omit or shorten** any part of the `css_selector`, `xpath`, or `outer_html` strings. You must copy them exactly as they appear in "dom_elements", even if it contains special characters like `.`, `[`, `]`, `{{`, `}}`, `"`, `'`, or `=`. These characters are all valid parts of DOM attributes.
6. If a Gherkin step does not refer to any DOM element, still output that step as a key with an object value, but each field ("css_selector", "xpath", "outer_html") should be empty strings.
7. Include **only** elements that actually match the Gherkin steps (e.g., “username”, “password”, “sign_button”). Ignore any unrelated DOM elements.
8. Return **only valid JSON**, with **no** additional commentary or text outside the JSON.
9. **Do not** generate or include Python or any other code. Do not wrap your JSON in Markdown or backticks. 
10. **Do not** add any commentary or explanation — only the JSON.
11. If a Gherkin step has no matched DOM elements, it must still appear with empty strings for those fields.
12. **Output only valid JSON** and nothing else — **no code snippets**, **no additional text**, **no explanations**.
13. If a value contains a period (`.`), it is part of the selector and must be kept. **Do NOT stop copying at `.` — it does NOT mean the end of a sentence.**

Here is your Gherkin content:
{gherkin}

Here are your DOM elements:
{dom_elements}

The example JSON output sturcture should be like
{example}

Remember, follow the requirements strictly. Output only valid JSON with curly braces, no extra text.
'''


prompt = PromptTemplate(
    input_variables=["gherkin", "dom_elements", "example"],
    template=template
)

example = '''

{
  "Given I open the product listing page \"http://localhost:3000/catalog\"": {
    "catalog_page": {
      "css_selector": "html > body > main > div.catalog-container",
      "xpath": "/html/body/main/div[1]",
      "outer_html": "<div class=\"catalog-container\" id=\"catalogArea\">...</div>"
    },
    "page_title": ""
  },
  "Then I choose the item \"FancyShoe123\" from the catalog": {
    "item_link": {
      "css_selector": ".product-item-link[data-item-id=\"FancyShoe123\"]",
      "xpath": "/html/body/main/div[1]/section/div[2]/a",
      "outer_html": "<a class=\"product-item-link\" data-item-id=\"FancyShoe123\" href=\"/product/FancyShoe123\">Fancy Shoe</a>"
    },
    "page_title": "Product Listing - My Store"
  },
  "And I click the add to cart button": {
    "add_to_cart_button": {
      "css_selector": "button.action-add-to-cart",
      "xpath": "/html/body/main/div[1]/section/div[2]/button",
      "outer_html": "<button class=\"action-add-to-cart\" data-item-id=\"FancyShoe123\">Add to Cart</button>"
    },
    "page_title": "Product Listing - My Store"
  },
  "Then I should see a success message and the page title is cart summary": {
    "confirmation_message": {
      "css_selector": "",
      "xpath": "",
      "outer_html": ""
    },
    "page_title": "Cart Summary - My Store"
  }
}

'''

gherkin_text = """Feature: Login to the Merchant Admin Portal

  Scenario: Successful login to the Merchant Admin Portal
    Given I open the login page "http://localhost:7780/admin"
    Then I enter username "newadmin" and password "NewAdmin123"
    And I click the sign button
    Then I should be logged in and the page title is dashboard
"""

dom_elements = '''
{
  "steps": [
    {
      "step_number": 1,
      "goal": "Open the login page 'http://localhost:7780/admin'",
      "page_title": "",
      "actions": [
        {
          "type": "go_to_url",
          "details": {
            "url": "http://localhost:7780/admin"
          }
        }
      ],
      "result": [
        "🔗  Navigated to http://localhost:7780/admin"
      ]
    },
    {
      "step_number": 2,
      "goal": "Enter username 'newadmin' into the username field",
      "page_title": "Magento Admin",
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
            "outer_html": "<input id=\"username\" class=\"admin__control-text\" type=\"text\" name=\"login[username]\" autofocus=\"\" value=\"\" data-validate=\"{required:true}\" placeholder=\"user name\" autocomplete=\"off\" browser-user-highlight-id=\"playwright-highlight-3\">"
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
            "outer_html": "<input id=\"login\" class=\"admin__control-text\" type=\"password\" name=\"login[password]\" data-validate=\"{required:true}\" value=\"\" placeholder=\"password\" autocomplete=\"off\" browser-user-highlight-id=\"playwright-highlight-5\">"
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
      "goal": "Check if you are logged in by verifying the page title or other indicators",
      "page_title": "Dashboard / Magento Admin",
      "actions": [
        {
          "type": "done",
          "details": {
            "text": "Successfully completed all steps: opened login page, entered credentials 'newadmin' and 'NewAdmin123', clicked sign-in button, and reached the dashboard page. Task accomplished."
          }
        }
      ],
      "result": [
        "Successfully completed all steps: opened login page, entered credentials 'newadmin' and 'NewAdmin123', clicked sign-in button, and reached the dashboard page. Task accomplished."
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
    "dom_elements": dom_elements,
    "example": example
})
print(result)

with open("output_raw4.txt", "w", encoding="utf-8") as f:
    f.write(result)

print("Successfully saved raw LLM output to output_raw.txt.")