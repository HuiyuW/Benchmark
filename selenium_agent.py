from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.options import Options
from langchain_ollama import ChatOllama

# 初始化浏览器
def init_driver():
    options = Options()
    options.add_argument('--window-size=1200,1000')
    driver = webdriver.Chrome(options=options)
    return driver

# 提取页面可交互元素信息
def get_clickable_elements(driver):
    elements = driver.find_elements(By.XPATH, "//*")
    clickable_elements = []

    for idx, el in enumerate(elements):
        try:
            tag = el.tag_name
            displayed = el.is_displayed()
            enabled = el.is_enabled()
            attrs = driver.execute_script(
                'var items={}; for(var i=0; i<arguments[0].attributes.length; i++){ items[arguments[0].attributes[i].name]=arguments[0].attributes[i].value }; return items;', el)

            if displayed and enabled and tag in ['input', 'button', 'a', 'select', 'textarea']:
                clickable_elements.append({
                    'index': idx,
                    'tag': tag,
                    'text': el.text.strip(),
                    'attributes': attrs
                })
        except Exception as e:
            continue

    return clickable_elements

# 构造 prompt 给 LLM
def build_prompt(elements, url, task):
    elements_description = "\n".join(
        [f"[{el['index']}] <{el['tag']} {el['attributes']}> {el['text']}" for el in elements])

    prompt = f"""
You are an agent interacting with a webpage.

Your task: {task}

Current page URL: {url}

Available interactive elements:
{elements_description}

Based on the elements listed above, describe your next action clearly in JSON format:
{{
  "next_action": "click/input/select",
  "element_index": number,
  "value": "text to input/select if applicable"
}}
"""
    return prompt

# 调用 LLM 推理下一步
def llm_inference(prompt):
    llm = ChatOllama(model="deepseek-r1:14b")
    response = llm.invoke(prompt)
    return response.content

# 主函数示例
def main():
    driver = init_driver()
    url = "http://localhost:4200"
    task = "Login with username 'user123' and password 'pass456'"

    driver.get(url)

    elements = get_clickable_elements(driver)
    prompt = build_prompt(elements, url, task)

    print("---Generated Prompt---\n")
    print(prompt)

    action = llm_inference(prompt)

    print("\n---LLM Suggested Action---\n")
    print(action)

    driver.quit()

if __name__ == "__main__":
    main()
