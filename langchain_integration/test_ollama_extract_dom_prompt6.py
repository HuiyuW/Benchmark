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
template = """
You are an intelligent assistant. Your task is to produce a structured JSON object mapping **each Gherkin step** to **exactly** the relevant DOM elements from the 'dom_elements' array. Follow these strict requirements:

1. **Output only valid JSON** – no extra text, no explanations, and no code snippets (e.g., **no Python**).
2. The top-level JSON is an object.
3. Each key in this object is the exact Gherkin step string (e.g., "Given I open the login page ...").
4. Each step’s value is an object that includes:
   - A **'page_title'** field (the page title if present in dom_elements for that step, or empty if none).
   - Zero or more descriptive keys for the relevant DOM elements (e.g., "username", "password", "sign_button"). Each such key maps to an object containing **exactly**: "css_selector", "xpath", "outer_html".
5. **Do not omit or shorten** any part of the `css_selector`, `xpath`, or `outer_html` strings. Copy them verbatim from the 'dom_elements' data, including any special characters like `.`, `[`, `]`, `"`, `'`, or `=`.
6. If a Gherkin step references no DOM element, show "page_title" plus empty values for each field you wish to keep (e.g., "css_selector": "", "xpath": "", "outer_html": "").
7. **Only** include elements that match the Gherkin step. No duplicates or irrelevant elements. For example, if the step is “And I click the sign button,” do not add “username” or “password” for that step.
8. If multiple actions in the 'dom_elements' come from the same step_number but the Gherkin steps are split, you must split them accordingly in the final JSON. Each Gherkin step is separate.
9. **Return only JSON** – do not include commentary, code snippets, or any additional text. Do not wrap the JSON in Markdown or backticks.
10. If a value contains a period (`.`), it is part of the selector. **Do not** interpret it as the end of a sentence or truncate anything after it.

Below is a small example of how each step has only its own "page_title" plus the relevant elements:

{example}

----

Now here is the **actual** Gherkin content to parse:
{gherkin}

----

Here are your `dom_elements`. Use them to find the correct "page_title" and correct elements for each step:
{dom_elements}

Remember: follow all instructions and produce valid JSON only.

"""


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

with open("output_raw6.txt", "w", encoding="utf-8") as f:
    f.write(result)

print("Successfully saved raw LLM output to output_raw.txt.")