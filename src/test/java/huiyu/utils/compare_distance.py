import json, re
from rapidfuzz.distance import Levenshtein, LCSseq   # pip install rapidfuzz
# from rapidfuzz.distance import Levenshtein
# from rapidfuzz.distance.LCS import distance as lcs_distance

def load_trace(path):
    """读取 trace.json → 提取 click / sendKeys / navigate 三类事件组成序列"""
    with open(path, encoding="utf-8") as f:
        events = json.load(f)

    seq = []
    for e in events:
        if e.get("eventType") not in ("click", "sendKeys", "navigate"):
            continue                # 过滤掉 exceptionListener 等
        selector = e.get("selector") or e.get("locator") or ""
        selector = re.sub(r":nth-child\(\d+\)", "", selector)  # 简单规范化
        seq.append(f"{e['eventType']}|{selector}")
    return seq

def compare_paths(ref_trace, cand_trace):
    """返回包含多种距离指标的结果 dict"""
    ref_seq  = load_trace(ref_trace)
    cand_seq = load_trace(cand_trace)

    # ① Levenshtein（编辑距离）及其归一化
    lev = Levenshtein.distance(ref_seq, cand_seq)
    lev_norm = lev / max(len(ref_seq), len(cand_seq))

    # ② LCS（最长公共子序列）比例
    lcs_len = max(len(ref_seq), len(cand_seq)) - LCSseq.distance(ref_seq, cand_seq)
    lcs_ratio = lcs_len / max(len(ref_seq), len(cand_seq))

    return {
        "refLen": len(ref_seq),
        "candLen": len(cand_seq),
        "levenshtein": lev,
        "levenshteinNorm": round(lev_norm, 3),
        "lcsLen": lcs_len,
        "lcsRatio": round(lcs_ratio, 3),
        "uiPathDistance": round(lev_norm, 3)
    }

if __name__ == "__main__":
    # 人工路径 & LLM 路径
    # result = compare_paths("C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/TenantSuccessSave_20250507_121356_446/trace.ui.trace.json", "C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/login_20250507_123727_551/trace.ui.trace.json")
    result = compare_paths("C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/TenantSuccessSave_20250507_121356_446/trace.ui.trace.json", "C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/deactivateUser_20250507_123505_154/trace.ui.trace.json")

    print(json.dumps(result, indent=2))
    #C:\Huiyu_Wang\Work\code\example_cucumber_langchain\demo\target\ui-benchmarks\login_20250507_123727_551\trace.ui.trace.json
    #target\ui-benchmarks\deactivateUser_20250507_123505_154