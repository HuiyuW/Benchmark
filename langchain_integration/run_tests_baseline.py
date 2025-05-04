# run_tests.py
import subprocess
import os
from datetime import datetime

def generate_java_file(filename: str, feature_name: str, output_path: str):

    # 模板内容
    java_template = f"""
package huiyu.stepdefinitions;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

@Suite
@SelectClasspathResource("features/{feature_name}.feature") 
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "huiyu.stepdefinitions_{filename}") 
public class CucumberTestRunner_{filename} {{
}}
"""

    try:
        # 创建保存目录（如果不存在）
        os.makedirs(os.path.dirname(output_path), exist_ok=True)

        # 保存内容到指定路径
        with open(output_path, 'w', encoding='utf-8') as java_file:
            java_file.write(java_template.strip())
        
        print(f"Java 文件已成功生成并保存到：{output_path}")
    except Exception as e:
        print(f"保存 Java 文件时出现错误：{e}")

def save_feature(output_path: str, feature):
    """
    将 feature 内容保存为 .feature 文件。
    
    参数：
    - output_path (str): 生成的 .feature 文件保存的完整路径（包含文件名）。
    """

    try:
        # 确保保存目录存在
        os.makedirs(os.path.dirname(output_path), exist_ok=True)
        
        # 保存内容到指定路径
        with open(output_path, 'w', encoding='utf-8') as feature_file:
            feature_file.write(feature.strip())
        
        print(f"Feature 文件已成功生成并保存到：{output_path}")
    except Exception as e:
        print(f"保存 Feature 文件时出现错误：{e}")




# def run_java_tests(filename):
#     project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
#     os.chdir(project_root)
    
#     command = f"mvn test -Dtest=CucumberTestRunner_{filename}"
#     process = subprocess.Popen(command, shell=True)
#     stdout, stderr = process.communicate()
    
#     if process.returncode == 0:
#         print("test success")
#         print(stdout)
#     else:
#         print("test fail")
#         print(stderr)


# def run_java_tests(filename, log_directory='logs'):
#     # 确保日志目录存在
#     if not os.path.exists(log_directory):
#         os.makedirs(log_directory)
    
#     # 设置日志文件名
#     timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
#     log_file_path = os.path.join(log_directory, f"test_log_{filename}_{timestamp}.log")
    
#     with open(log_file_path, "w", encoding="utf-8") as log_file:
#         project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
#         os.chdir(project_root)
        
#         command = f"mvn test -Dtest=CucumberTestRunner_{filename}"
        
#         # 保存输出与错误到日志文件中
#         process = subprocess.Popen(
#             command, 
#             shell=True, 
#             stdout=log_file, 
#             stderr=log_file
#         )
#         process.communicate()
        
#     if process.returncode == 0:
#         print(f"Test success. Log saved to: {log_file_path}")
#     else:
#         print(f"Test failed. Log saved to: {log_file_path}")


def run_java_tests(filename, log_directory='logs'):
    # 确保日志目录存在
    if not os.path.exists(log_directory):
        os.makedirs(log_directory)
    
    # 设置日志文件名
    timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
    log_file_path = os.path.join(log_directory, f"test_log_{filename}_{timestamp}.log")
    
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    os.chdir(project_root)
    
    command = f"mvn test -Dtest=CucumberTestRunner_{filename}"
    
    # 打开日志文件用于写入
    with open(log_file_path, "w", encoding="utf-8") as log_file:
        # 创建一个子进程用于执行命令，并实时捕捉输出
        process = subprocess.Popen(
            command,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            bufsize=1,
            universal_newlines=True
        )

        # 实时读取输出并写入文件，同时显示在 Terminal
        for line in process.stdout:
            print(line, end="")  # 在终端中实时显示
            log_file.write(line)  # 写入到日志文件

        # 等待进程结束
        process.wait()
        
    if process.returncode == 0:
        print(f"\nTest success. Log saved to: {log_file_path}")
    else:
        print(f"\nTest failed. Log saved to: {log_file_path}")



