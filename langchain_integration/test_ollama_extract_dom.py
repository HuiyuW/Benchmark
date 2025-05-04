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
"
        "You are an intelligent assistant that helps align Gherkin feature steps with actual DOM elements.\n"
        "As you know that if people write step definition in selenium accroding to gherkin feature file, people need to know the exact dom entries and elements from each step.\n"
        "Your goal is to match each meaningful step term with the correct DOM element from the given DOM interactions at each single step.\n"

        
        "Your Task: Please Output a structured JSON object like this:\n\n"
        "{{\n"
        "  \"Given...\": {{ DOM_element_name1 {{\"css_selector\": ..., \"xpath\": ..., \"matched_text\": ... }}, DOM_element_name2 {{\"css_selector\": ..., \"xpath\": ..., \"matched_text\": ... }},}},\n"
        "  \"Then...\": {{ DOM_element_name1 {{\"css_selector\": ..., \"xpath\": ..., \"matched_text\": ... }}, DOM_element_name2 {{\"css_selector\": ..., \"xpath\": ..., \"matched_text\": ... }},}},\n"
        "  \"When...\": {{ DOM_element_name1 {{\"css_selector\": ..., \"xpath\": ..., \"matched_text\": ... }}, DOM_element_name2 {{\"css_selector\": ..., \"xpath\": ..., \"matched_text\": ... }},}},\n"
        "}}\n\n"

        Instructions
        "Replace Given, Then, When from above output Json structure with actual gherkin feature step and replace DOM_element_name1 with the keyword that you matched from gherkin feature.\n"
        "Use your best semantic reasoning to infer mappings based on the HTML tag, id, class, type, placeholder, or visible text.\n"
        "Only return DOM entries that are relevant for matching actual user actions in Gherkin.\n"
        "If you find the matched key word from gherkin feature file and DOM element, copy the whole content from dom_elements to each output css_selector and xpath.\n"
        "DO NOT include unrelated elements."

        "gherkin_feature": {gherkin}

        "dom_elements": {dom_elements}

        "Your Task: Please Output a structured JSON object as above ONLY. Do not return any explanation text or comments. Just valid JSON content enclosed with curly braces `{{}}`\n\n"
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

# matches = re.findall(r'\{[\s\S]*?\}', result.strip())

# if matches:
#     for idx, match in enumerate(matches):
#         try:
#             json_obj = json.loads(match)
#             output_path = f"matched_dom_mapping_step_{idx+1}.json"
#             with open(output_path, 'w', encoding='utf-8') as f:
#                 json.dump(json_obj, f, indent=2, ensure_ascii=False)
#             print(f"\n✅ Extracted and saved JSON successfully to: {output_path}")
#             break
#         except json.JSONDecodeError as e:
#             print(f"⚠️ JSON decode error in match {idx+1}: {e}")
# else:
#     print("❌ No JSON found in LLM output.")