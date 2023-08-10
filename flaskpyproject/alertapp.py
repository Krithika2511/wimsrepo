from flask import Flask, render_template
import json

app = Flask(__name__)

# Sample JSON data for the machines
json_data_1 = """
{
    "Last Boot Time (IST)":  "23-08-08 09:02:10",
    "CPU Utilized":  "35 %",
    "Hostname":  "Machine2",
    "Total Memory":  "4.61 GB",
    "Memory Utilized":  "29.78 %",
    "Time":  "2023-08-08 09:04:19",
    "Virtual Processors(CPU)":  "3 (Total: 1)",
    "Recent patch installed":  "KB3200970 - Security Update - 11/21/2016 00:00:00",
    "Drives present in the Machine":  [
        "Drive: A: - Size: 0 GB - Free Space: 0 GB",
        "Drive: C: - Size: 40 GB - Free Space: 26.19 GB",
        "Drive: D: - Size: 0.05 GB - Free Space: 0 GB",
        "Drive: H: - Size: 232.01 GB - Free Space: 16.03 GB"
    ],
    "IP Address":  [
        "192.168.10.2",
        "192.168.0.135",
        "127.0.0.1"
    ]
}
"""

json_data_2 = """
{
    "Last Boot Time (IST)":  "23-08-08 08:24:44",
    "CPU Utilized":  "0 %",
    "Hostname":  "Machine3",
    "Total Memory":  "4.77 GB",
    "Memory Utilized":  "26.25 %",
    "Time":  "2023-08-08 09:04:56",
    "Virtual Processors(CPU)":  "3 (Total: 1)",
    "Recent patch installed":  "KB4103723 - Security Update - 08/01/2023 00:00:00",
    "Drives present in the Machine":  [
        "Drive: A: - Size: 0 GB - Free Space: 0 GB",
        "Drive: C: - Size: 50 GB - Free Space: 23.83 GB",
        "Drive: D: - Size: 0.05 GB - Free Space: 0 GB",
        "Drive: H: - Size: 232.01 GB - Free Space: 16.03 GB"
    ],
    "IP Address":  [
        "192.168.10.3",
        "192.168.0.136",
        "127.0.0.1"
    ]
}
"""

@app.route('/')
def index():
    data = [json.loads(json_data_1), json.loads(json_data_2)]
    return render_template('index.html', data=data)


if __name__ == '__main__':
    app.run(debug=True)
