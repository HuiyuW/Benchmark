from langchain_integration.generate_overall_baseline import generate_step_definitions
from langchain_integration.run_tests_baseline import run_java_tests, generate_java_file, save_feature
import os
import shutil

def create_folder(path: str) -> None:
    """
    在指定路径下创建文件夹。如果文件夹已存在，则不会重复创建。
    
    参数:
    - path (str): 要创建的文件夹路径。
    """
    try:
        # 检查文件夹是否存在
        if not os.path.exists(path):
            # 创建文件夹
            os.makedirs(path)
            print(f"文件夹已成功创建：{path}")
        else:
            print(f"文件夹已存在：{path}")
    except Exception as e:
        print(f"创建文件夹时发生错误：{e}")

feature_file = '''
Feature: Tenant Management

  Scenario: Create and verify a new Tenant
    Given I open the login page "http://localhost:4200/"
    Then I should see Username APIKey and Login Button on the login page
    When I enter username "vw2xn87" and APIKey "xOqMiHJegL3JvnOCK9rb0vOG1pZ7Oc1C7fAfRR0WEuk4keuwEc0yAUF3QSahP8G3IBKw2FO2zvcr7njD"
    And I click the login button
    Then I should see the terms and conditions popup
    When I move to the accept button to ensure it is visible
    And I click the accept button
    Then I should be logged in and see my profile avatar
    When I switch the portal language to "en"
    When I open the Administration dropdown in the top navigation
    And I select Tenants from the Administration dropdown
    And I click the New Tenant button
    And I type tenant name "test0426"
    And I click the Save Tenant button
    Then the tenant "test0426" appears in the list
    '''


template = '''
<|begin_of_text|><|start_header_id|>system<|end_header_id|>
Cutting Knowledge Date: December 2023
Today Date: Jan 19, 2025
You are a helpful assistant that can read a Gherkin Feature file and produce Java-based Selenium Step Definitions. 

Your tasks:

- For each scenario/step in the user's Gherkin Feature file, provide a **fully implemented** Step Definition method in Java.
- Avoid empty or placeholder implementations; fill in as much detail as possible.

**Important Reminders**:
1. The final code should look like typical Selenium step definitions, one method for each Gherkin step.  
2. Output only the Java code. No additional text or Markdown formatting.

<|start_header_id|>user<|end_header_id|>
Please generate the Java Selenium Step Definitions to fully cover the provided Gherkin Feature File
{task}
<|eot_id|><|start_header_id|>assistant<|end_header_id|>
'''
add_java_header = False
filename = 'tenant_success_save_ds'
feature_name = 'tenant_success_save_ds'
feature_path = f"src/test/resources/features/{feature_name}.feature"
log_directory = 'results/Baseline_exp_new'
output_path = f"src/test/java/huiyu/stepdefinitions/CucumberTestRunner_{filename}.java"
save_feature(feature_path, feature_file)

with open(feature_path, "r") as f:
    feature_text = f.read()
save_folder = f"src/test/java/huiyu/stepdefinitions_{filename}"
create_folder(save_folder)
saved_step_path = f"{save_folder}/{filename}.java"

timespan_log_directory = generate_step_definitions(feature_text, "ollama","deepseek-coder:6.7b-instruct", saved_step_path, filename, prompt_template=template,log_directory = log_directory, add_java_header = add_java_header)
# timespan_log_directory = generate_step_definitions(feature_text, "ollama","llama3.1:latest", saved_step_path, filename, prompt_template=template,log_directory = log_directory, add_java_header = add_java_header)

# log_directory = generate_step_definitions(feature_text, "ollama","llama3.1:latest", saved_step_path, filename, prompt_template=template, log_directory = log_directory)

# steps_path = generate_step_definitions(feature_text, "huggingface","C:/Huiyu_Wang/Work/code/LLM/deepseek-coder-6.7b-instruct")
# steps_path = generate_step_definitions(feature_text, "vw_llm")
print(f"Step definition is generated at {saved_step_path}")


dest_path = os.path.join(timespan_log_directory, os.path.basename(saved_step_path))
shutil.copy(saved_step_path, dest_path)
generate_java_file(filename, feature_name, output_path)#cucumber runner
run_java_tests(filename, timespan_log_directory)
