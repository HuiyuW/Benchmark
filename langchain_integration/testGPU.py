import os
import time
from transformers import AutoTokenizer, LlamaForCausalLM, pipeline, AutoModelForCausalLM
from langchain import LLMChain, HuggingFacePipeline, PromptTemplate
import torch
from datetime import datetime
import gc


model_paths = [
    # "/workspace/Llama-3.1-8B-Instruct",
    "C:/Huiyu_Wang/Work/code/LLM/deepseek-coder-6.7b-instruct"
    # "/workspace/starcoder2-15b",
    # "/workspace/starcoder2-7b",
]


device = torch.device("cuda:0")


template = '''
I am using Java + Selenium + Cucumber for automated testing, with the following setup:

Java Version: 11
Testing Framework: JUnit 4
Selenium Version: 4.11.0
Cucumber-Java Version: 7+
All Step Definitions classes go in a steps package.
There is a Hooks class where @Before and @After manage WebDriver initialization and teardown, accessed via Hooks.driver.
Desired Output Format
No repetition of the entire prompt in your final output.
For each Gherkin step, provide a @Given, @When, or @Then-annotated Java method in the Step Definitions file.
Use Selenium to locate elements and perform actions/validations.
Use placeholders (By.id("TODO"), // TODO comments) if any locator/assertion is not determined.
The final code should start with:
package steps;

and include necessary imports like:

import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import static org.junit.Assert.*;

Example of Gherkin + Step Definitions
Sample Gherkin
Feature: Login Feature
  Scenario: Login with valid credentials
    Given I open the login page
    When I fill in username "validUser" and password "secretPass"
    And I click on the login button
    Then I should see the dashboard

Sample Step Definitions
package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import static org.junit.Assert.assertTrue;

public class LoginStepDefs {{

    private WebDriver driver;

    public LoginStepDefs() {{
        this.driver = Hooks.driver;
    }}

    @Given("I open the login page")
    public void iOpenTheLoginPage() {{
        driver.get("https://example.com/login");
    }}

    @When("I fill in username {{string}} and password {{string}}")
    public void iFillInUsernameAndPassword(String username, String password) {{
        driver.findElement(By.id("username_field")).sendKeys(username);
        driver.findElement(By.id("password_field")).sendKeys(password);
    }}

    @When("I click on the login button")
    public void iClickOnTheLoginButton() {{
        driver.findElement(By.id("login_button")).click();
    }}

    @Then("I should see the dashboard")
    public void iShouldSeeTheDashboard() {{
        boolean dashboardVisible = driver.findElement(By.id("dashboard_page")).isDisplayed();
        assertTrue("Dashboard should be visible after login", dashboardVisible);
    }}
}}

New Gherkin Feature to Process
Please generate Step Definitions directly (without repeating this prompt) for:
{task}

Instructions
Provide a Java class, package steps;, necessary imports, and a WebDriver instance.
Write separate annotated methods for each step.
Use placeholders if locators or assertions are unknown.
##Do not repeat the entire prompt text in your output. Just output the Java code and optionally a brief explanation after.
### Start Output
Output begins below:
'''

task_gherkin = '''
  Scenario: View job list and job detail
    Given I am on the T-Rex dashboard after logging in
    When I click on "Jobs" in the main menu
    Then I should see the job list
    When I select one job from the list
    Then I should see the job detail page
'''
prompt = PromptTemplate(
    input_variables=["task"],
    template=template
)


for model_path in model_paths:

    model_name = os.path.basename(model_path)
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    log_file = f"step_definition_withexample_{timestamp}_{model_name}.txt"
    with open(log_file, "w", encoding="utf-8") as log:

        log.write(f"Model Path: {model_path}\n")
        log.write(f"Device: {device}\n")
        log.write("Parameters: max_length=2048, top_p=1, repetition_penalty=1.15\n\n")


        tokenizer = AutoTokenizer.from_pretrained(model_path)
        
        model = AutoModelForCausalLM.from_pretrained(model_path, torch_dtype=torch.float16, device_map=device)
        pipe = pipeline(
            "text-generation",
            model=model,
            tokenizer=tokenizer,
            max_length=2048,
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
            test_end = time.time()

            num_tokens = len(tokenizer.encode(result))
            total_tokens += num_tokens
            tokens_per_second = num_tokens / (test_end - test_start)

   
            log.write(f"Output:\n{result}\n")
            print(f"Output:\n{result}\n")
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
    del chain
    del llama_model
    del pipe
    del model
    del tokenizer


    gc.collect()

    torch.cuda.empty_cache()

print("All experiments completed.")
