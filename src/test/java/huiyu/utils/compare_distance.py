import json, re
from rapidfuzz.distance import Levenshtein, LCSseq


def load_trace(path):

    with open(path, encoding="utf-8") as f:
        events = json.load(f)

    seq = []
    for e in events:
        if e.get("eventType") not in ("click", "sendKeys", "navigate","findElement"):
            continue                
        selector = e.get("selector") or e.get("locator") or ""
        selector = re.sub(r":nth-child\(\d+\)", "", selector)  
        seq.append(f"{e['eventType']}|{selector}")
    return seq

def compare_paths(ref_trace, cand_trace):

    ref_seq  = load_trace(ref_trace)
    cand_seq = load_trace(cand_trace)


    lev = Levenshtein.distance(ref_seq, cand_seq)
    lev_norm = lev / max(len(ref_seq), len(cand_seq))


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
    # result = compare_paths("C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/TenantSuccessSave_20250507_121356_446/trace.ui.trace.json", "C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/login_20250507_123727_551/trace.ui.trace.json")
    # result = compare_paths("C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/TenantSuccessSave_20250507_121356_446/trace.ui.trace.json", "C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/deactivateUser_20250507_123505_154/trace.ui.trace.json")
    # result = compare_paths("C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/tenant_blank_red_error_20250507_153403_407/trace.ui.trace.json", "C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/TenantSuccessSave_20250507_153023_095/trace.ui.trace.json")
    result = compare_paths("C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/tenant_blank_red_error_20250507_153403_407/trace.ui.trace.json", "C:/Huiyu_Wang/Work/code/example_cucumber_langchain/demo/target/ui-benchmarks/tenant_target_blank_error_20250507_170236_231/trace.ui.trace.json")

    print(json.dumps(result, indent=2))