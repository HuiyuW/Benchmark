from langchain_integration.generate_gherkin import generate_gherkin
from langchain_integration.generate_overall import generate_step_definitions
from langchain_integration.run_tests import run_java_tests
import os

from datetime import datetime

import asyncio
import sys
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "browser_use"))
from examples.models.browser_use_agent import run_browser_agent
from extract_json_func import simplify_agent_history
from map_gherkin_dom import map_gherkin_to_dom_elements

async def main():
    
    timestamp = datetime.now().strftime("%Y-%m-%d_%H%M%S")
    base_result_dir = os.path.join(os.path.dirname(__file__), "results", timestamp)
    os.makedirs(base_result_dir, exist_ok=True)
    print(f"Results will be saved in: {base_result_dir}")

    
    agent_history_path = os.path.join(base_result_dir, "path_login.json")
    simplified_history_path = os.path.join(base_result_dir, "simplified_steps_generic.json")
    dom_elements_path = os.path.join(base_result_dir, "output_raw.txt")


    feature_path = "C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/src/test/resources/features/LoginTest.feature"
    with open(feature_path, "r") as f:
        feature_text = f.read()


        
    await run_browser_agent(feature_text, agent_history_path)
    simplify_agent_history(agent_history_path, simplified_history_path)
    map_gherkin_to_dom_elements(feature_text, simplified_history_path, dom_elements_path, "deepseek-r1:14b")
    with open(dom_elements_path, "r") as f:
        dom_element = f.read()
    steps_path = generate_step_definitions(feature_text, dom_element, "ollama", "deepseek-coder-v2:16b")
    # steps_path = generate_step_definitions(feature_text, "huggingface","C:/Huiyu_Wang/Work/code/LLM/deepseek-coder-6.7b-instruct")
    # steps_path = generate_step_definitions(feature_text, "vw_llm")
    print(f"Step definition is generated at {steps_path}")


    run_java_tests()

if __name__ == "__main__":
    asyncio.run(main())
