#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
将Markdown文件转换为HTML，然后用Word打开
"""

import re

def convert_md_to_html(md_file, html_file):
    """转换Markdown到HTML"""
    print(f"开始转换: {md_file}")
    
    # 读取Markdown文件
    with open(md_file, 'r', encoding='utf-8') as f:
        content = f.read()
    
    print(f"文件读取成功，内容长度: {len(content)} 字符")
    
    html_content = []
    html_content.append('<!DOCTYPE html>')
    html_content.append('<html>')
    html_content.append('<head>')
    html_content.append('<meta charset="UTF-8">')
    html_content.append('<title>毕业设计说明书</title>')
    html_content.append('<style>')
    html_content.append('body { font-family: "宋体", SimSun, serif; font-size: 12pt; line-height: 1.8; max-width: 800px; margin: 0 auto; padding: 20px; }')
    html_content.append('h1 { font-family: "黑体", SimHei, sans-serif; font-size: 16pt; text-align: center; font-weight: bold; margin-top: 24pt; margin-bottom: 12pt; }')
    html_content.append('h2 { font-family: "黑体", SimHei, sans-serif; font-size: 14pt; font-weight: bold; margin-top: 18pt; margin-bottom: 12pt; }')
    html_content.append('h3 { font-family: "黑体", SimHei, sans-serif; font-size: 12pt; font-weight: bold; margin-top: 12pt; margin-bottom: 6pt; }')
    html_content.append('h4 { font-family: "黑体", SimHei, sans-serif; font-size: 12pt; font-weight: bold; margin-top: 12pt; margin-bottom: 6pt; }')
    html_content.append('p { margin: 6pt 0; text-indent: 2em; }')
    html_content.append('code { font-family: "Courier New", monospace; font-size: 10pt; color: #000080; background-color: #f5f5f5; padding: 2px 4px; }')
    html_content.append('pre { background-color: #f5f5f5; padding: 10px; border-radius: 4px; overflow-x: auto; }')
    html_content.append('pre code { background-color: transparent; padding: 0; }')
    html_content.append('table { border-collapse: collapse; width: 100%; margin: 12pt 0; }')
    html_content.append('th, td { border: 1px solid #000; padding: 8px; text-align: left; }')
    html_content.append('th { background-color: #f0f0f0; font-weight: bold; }')
    html_content.append('ul, ol { margin: 6pt 0; padding-left: 2em; }')
    html_content.append('li { margin: 3pt 0; }')
    html_content.append('strong { font-weight: bold; }')
    html_content.append('</style>')
    html_content.append('</head>')
    html_content.append('<body>')
    
    # 按行处理
    lines = content.split('\n')
    in_code_block = False
    code_lines = []
    
    i = 0
    while i < len(lines):
        line = lines[i]
        
        # 处理代码块
        if line.strip().startswith('```'):
            if in_code_block:
                # 结束代码块
                html_content.append('<pre><code>')
                html_content.append('\n'.join(code_lines))
                html_content.append('</code></pre>')
                code_lines = []
                in_code_block = False
            else:
                # 开始代码块
                in_code_block = True
            i += 1
            continue
        
        if in_code_block:
            code_lines.append(line)
            i += 1
            continue
        
        # 跳过空行
        if not line.strip():
            i += 1
            continue
        
        stripped = line.strip()
        
        # 处理标题
        if stripped.startswith('# '):
            text = stripped[2:]
            html_content.append(f'<h1>{text}</h1>')
        elif stripped.startswith('## '):
            text = stripped[3:]
            html_content.append(f'<h2>{text}</h2>')
        elif stripped.startswith('### '):
            text = stripped[4:]
            html_content.append(f'<h3>{text}</h3>')
        elif stripped.startswith('#### '):
            text = stripped[5:]
            html_content.append(f'<h4>{text}</h4>')
        
        # 处理表格
        elif stripped.startswith('|') and i + 1 < len(lines) and '---' in lines[i + 1]:
            headers = [cell.strip() for cell in stripped.split('|')[1:-1]]
            i += 2  # 跳过分隔行
            
            html_content.append('<table>')
            html_content.append('<thead>')
            html_content.append('<tr>')
            for header in headers:
                html_content.append(f'<th>{header}</th>')
            html_content.append('</tr>')
            html_content.append('</thead>')
            html_content.append('<tbody>')
            
            while i < len(lines) and lines[i].strip().startswith('|'):
                row_data = [cell.strip() for cell in lines[i].split('|')[1:-1]]
                if len(row_data) == len(headers):
                    html_content.append('<tr>')
                    for cell_data in row_data:
                        html_content.append(f'<td>{cell_data}</td>')
                    html_content.append('</tr>')
                i += 1
            
            html_content.append('</tbody>')
            html_content.append('</table>')
            continue
        
        # 处理列表
        elif stripped.startswith('- ') or stripped.startswith('* '):
            text = stripped[2:]
            text = re.sub(r'\*\*(.*?)\*\*', r'<strong>\1</strong>', text)
            html_content.append(f'<p style="text-indent: 0; margin-left: 2em;">• {text}</p>')
        
        elif re.match(r'^\d+\. ', stripped):
            text = re.sub(r'^\d+\. ', '', stripped)
            text = re.sub(r'\*\*(.*?)\*\*', r'<strong>\1</strong>', text)
            html_content.append(f'<p style="text-indent: 0; margin-left: 2em;">{text}</p>')
        
        # 处理普通段落
        else:
            text = stripped
            text = re.sub(r'\*\*(.*?)\*\*', r'<strong>\1</strong>', text)
            html_content.append(f'<p>{text}</p>')
        
        i += 1
    
    html_content.append('</body>')
    html_content.append('</html>')
    
    # 保存HTML文件
    with open(html_file, 'w', encoding='utf-8') as f:
        f.write('\n'.join(html_content))
    
    print(f"转换完成！HTML文档已保存至: {html_file}")
    print("您可以用Microsoft Word打开此HTML文件，然后另存为.docx格式")

if __name__ == '__main__':
    md_file = r'c:\Users\Liu17\OneDrive\Desktop\lhh35\毕业设计说明书_8000字版.md'
    html_file = r'c:\Users\Liu17\OneDrive\Desktop\lhh35\毕业设计说明书_8000字版.html'
    convert_md_to_html(md_file, html_file)
