# main.py
from langchain_integration.generate_gherkin import generate_gherkin
# from langchain_integration.generate_gherkin_VB import generate_gherkin
# from langchain_integration.generate_gherkin_langchain import generate_gherkin
from langchain_integration.generate_overall import generate_step_definitions
from langchain_integration.run_tests import run_java_tests



# task_description = "Open a webpage and verify the page title"


# feature_path = generate_gherkin(task_description)
# print(f"Gherkin scripts saved at {feature_path}")

feature_path = "C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/src/test/resources/features/LoginTest.feature"
with open(feature_path, "r") as f:
    feature_text = f.read()

dom_element = '''
- **Username Input**: `By.cssSelector("groupui-input[id='welcome-user-name']")`
- **APIKey Input**: `By.cssSelector("groupui-input[id='welcome-api-key']")`
- **Login Button**: `By.cssSelector("#welcome-login")`
- **Terms & Conditions Headline**: `By.xpath("//groupui-headline[contains(text(),'Terms & Conditions')]")`
- **Accept Button**: `By.cssSelector("groupui-button#welcome-agree-agb")`
- **Profile Header**: `By.cssSelector("#app-profile-header")`
- **Profile Avatar**: `By.cssSelector("groupui-avatar")`
- **Toast Title**: `By.cssSelector(".toast-title")`
- **Logout Button**: `By.cssSelector("#app-logout")`
'''
steps_path = generate_step_definitions(feature_text, dom_element, "ollama","deepseek-coder-v2:16b")
# steps_path = generate_step_definitions(feature_text, "huggingface","C:/Huiyu_Wang/Work/code/LLM/deepseek-coder-6.7b-instruct")
# steps_path = generate_step_definitions(feature_text, "vw_llm")
print(f"Step definition is generated at {steps_path}")


run_java_tests()
