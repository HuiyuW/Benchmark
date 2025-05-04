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

template = '''
<|begin_of_text|><|start_header_id|>system<|end_header_id|>

Cutting Knowledge Date: December 2023
Today Date: Jan 19, 2025

You are a helpful assistant that can read a Gherkin Feature file and produce Java-based Selenium Step Definitions. You will be given:

1. An **example Gherkin Feature file** and **its corresponding Step Definition** as references, so you know how the final result should look.
2. A **pinned portion** of the Step Definition code (e.g., dependencies, imports, @Before annotations), which must remain intact. You must only generate the remaining part of the code.

Your tasks:

- For each scenario/step in the user's Gherkin Feature file, provide a **fully implemented** Step Definition method in Java.
- Ensure the code is **ready to run** in Selenium without requiring placeholders or JavaScript.
- **Use Selenium’s native methods** for actions such as scrolling or clicking buttons; do **not** rely on custom JavaScript execution.
- Never generate an entire script in another language (like Python). Just produce **Java** code for the Step Definitions.
- Keep the pinned code sections exactly as given and **only** produce the additional code needed to complete the Step Definition.
- Avoid empty or placeholder implementations; fill in as much detail as possible.
- Final output should be **directly usable** in a typical Selenium/JUnit (or TestNG) project.

**Important Reminders**:
1. The final code should look like typical Selenium + JUnit (or TestNG) step definitions, one method for each Gherkin step.  
2. Output only the Java code (pinned section + your new steps). No additional text or Markdown formatting.


<|eot_id|><|start_header_id|>user<|end_header_id|>

Below is my input. Please read all parts, then generate the requested Step Definition code (excluding the pinned section, which is already provided):

**1. Pinned Step Definition Code (Imports, Dependencies, @Before, etc.)**  

package huiyu.stepdefinitions_baseline;

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

public class baseline extends BaseTest {{

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

filename = 'baseline_ds_login'
feature_name = 'baseline_ds_login'
feature_path = f"src/test/resources/features/{feature_name}.feature"
log_directory = 'results/baseline_exp'
output_path = f"src/test/java/huiyu/stepdefinitions/CucumberTestRunner_{filename}.java"
save_feature(feature_path)

with open(feature_path, "r") as f:
    feature_text = f.read()
save_folder = f"src/test/java/huiyu/stepdefinitions_{filename}"
create_folder(save_folder)
saved_step_path = f"{save_folder}/{filename}.java"

generate_step_definitions(feature_text, "ollama","deepseek-coder:6.7b-instruct", saved_step_path, filename, prompt_template=template,log_directory = log_directory)
# log_directory = generate_step_definitions(feature_text, "ollama","llama3.1:latest", saved_step_path, filename, prompt_template=template, log_directory = log_directory)

# steps_path = generate_step_definitions(feature_text, "huggingface","C:/Huiyu_Wang/Work/code/LLM/deepseek-coder-6.7b-instruct")
# steps_path = generate_step_definitions(feature_text, "vw_llm")
print(f"Step definition is generated at {saved_step_path}")


dest_path = os.path.join(log_directory, os.path.basename(saved_step_path))
shutil.copy(saved_step_path, dest_path)
generate_java_file(filename, feature_name, output_path)
run_java_tests(filename, log_directory)
