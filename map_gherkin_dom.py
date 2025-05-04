import os
import json
import re
from langchain import LLMChain, PromptTemplate
from langchain_ollama import ChatOllama

def clean_and_save_output_as_txt(text: str, output_txt_path: str):
    """
    从 LLM 输出中移除 <think> ... </think> 内容，并将剩余文本保存为 TXT 文件。

    参数:
        text: LLM 输出完整文本
        output_txt_path: TXT 文件保存路径
    """

    # Step 1: 移除 <think> ... </think> 区块
    cleaned_text = re.sub(r"<think>.*?</think>", "", text, flags=re.DOTALL).strip()

    # Step 2: 保存为 TXT 文件
    os.makedirs(os.path.dirname(output_txt_path), exist_ok=True)
    with open(output_txt_path, "w", encoding="utf-8") as f:
        f.write(cleaned_text)

    print(f"remove <think> and save TXT：{output_txt_path}")

def map_gherkin_to_dom_elements(
    gherkin_text: str,
    dom_json_path: str,
    result_output_path: str,
    model_name: str = "deepseek-r1:14b"
):
    """
    使用 LLM 把 Gherkin step 和 DOM 元素进行结构化匹配，保存结果为 JSON/TXT。

    参数：
        gherkin_text: Gherkin Feature 的字符串内容
        dom_json_path: 存放 DOM 元素的 JSON 文件路径
        result_output_path: 保存最终 LLM 输出结果的文件路径
        model_name: 使用的 Ollama 模型名，默认 deepseek-coder:6.7b
    """

    # Step 1: 读取 dom_elements JSON 文件
    if not os.path.exists(dom_json_path):
        raise FileNotFoundError(f"DOM JSON file not found: {dom_json_path}")
    
    with open(dom_json_path, "r", encoding="utf-8") as f:
        dom_elements = f.read()

    # Step 2: 准备 Prompt 模板
    template = """
You are an intelligent assistant. Your task is to produce a structured JSON object mapping **each Gherkin step** to **exactly** the relevant DOM elements from the 'dom_elements' array. Each step in the final JSON must have:

- A **'page_title'** field at the top level (the page title from the matching step in dom_elements, or empty if none).
- **Only** the DOM elements that belong to that step. For instance, if the user is entering username/password, then only those fields appear. If the user is clicking the sign button, only that button appears, etc.
- If a step does not use any DOM elements, you must still output that step with a 'page_title' plus empty strings for 'css_selector', 'xpath', 'outer_html' (or omit the elements entirely if you prefer).
- **No** duplication or irrelevant elements. Do not copy 'username' or 'password' into steps that only involve a button click, and vice versa.
- **Do not** generate or include Python or any other code. Do not wrap your JSON in Markdown or backticks. 
- **Do not** add any commentary or explanation — only the JSON.

**Structure Requirements**:
1. The top-level JSON is an object.
2. Each key is the exact Gherkin step string (e.g. "Given I open the login page '...'").
3. Each value is an object with:
   - A **'page_title'** field (string).
   - Zero or more keys for each relevant DOM element (e.g. "username", "password", "sign_button").  
     Each of these is again an object with **exactly**: "css_selector", "xpath", "outer_html".
4. When copying "css_selector", "xpath", or "outer_html" from 'dom_elements', use them verbatim (no truncation, no rewriting, no skipping periods).
5. Return **only valid JSON** with no extra explanations or code.

Below is a small example of the format we want. Notice how each step has one "page_title" at the top and only relevant elements. Steps that do not use an element have an empty object or empty strings:

{example}

----

Now here is the **actual** Gherkin content you must parse:
{gherkin}

----

Here are your `dom_elements`. Use them to find the correct "page_title" and correct elements for each step, splitting them appropriately if multiple actions exist in a single step_number but correspond to different Gherkin steps:
{dom_elements}

**Important**:
- If "Then I enter username ... and password ..." is separate from "And I click the sign button" in the Gherkin, but both appear in step_number=2 in dom_elements, you must separate them accordingly in your final JSON. Do not merge them into one step's object.
- If a Gherkin step references no elements, show "page_title" plus empty fields or an empty object for the element.  
- Output only JSON, no extra text.

Remember, follow the requirements strictly. Output only valid JSON with curly braces, no extra text. Don't return any python solution, Python script or any code solution.
"""

    example = '''
{
  "Given I open the product listing page \\"http://localhost:3000/catalog\\"": {
    "catalog_page": {
      "css_selector": "html > body > main > div.catalog-container",
      "xpath": "/html/body/main/div[1]",
      "outer_html": "<div class=\\"catalog-container\\" id=\\"catalogArea\\">...</div>"
    },
    "page_title": ""
  },
  "Then I choose the item \\"FancyShoe123\\" from the catalog": {
    "item_link": {
      "css_selector": ".product-item-link[data-item-id=\\"FancyShoe123\\"]",
      "xpath": "/html/body/main/div[1]/section/div[2]/a",
      "outer_html": "<a class=\\"product-item-link\\" data-item-id=\\"FancyShoe123\\" href=\\"/product/FancyShoe123\\">Fancy Shoe</a>"
    },
    "page_title": "Product Listing - My Store"
  },
  "And I click the add to cart button": {
    "add_to_cart_button": {
      "css_selector": "button.action-add-to-cart",
      "xpath": "/html/body/main/div[1]/section/div[2]/button",
      "outer_html": "<button class=\\"action-add-to-cart\\" data-item-id=\\"FancyShoe123\\">Add to Cart</button>"
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

    prompt = PromptTemplate(
        input_variables=["gherkin", "dom_elements", "example"],
        template=template
    )

    # Step 3: 初始化 LLM
    llm = ChatOllama(
        model=model_name,
        num_ctx=6000,
        temperature=0.0,
        top_k=1,
        top_p=1.0
    )

    chain = LLMChain(llm=llm, prompt=prompt)

    # Step 4: 执行推理
    result = chain.run({
        "gherkin": gherkin_text,
        "dom_elements": dom_elements,
        "example": example
    })


    print(result)
    clean_and_save_output_as_txt(result, result_output_path)