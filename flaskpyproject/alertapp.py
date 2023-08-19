from flask import Flask, render_template
import json
import os 
app = Flask(__name__)
json_files = []
directory = "Test"
print(directory)
# Sample JSON data for the machines
for filename in os.listdir(directory):
    if filename.endswith(".json"):
        file = open(directory + "/" + filename)
        json_files.append(json.load(file))                         
print(json_files)
@app.route('/')
def index():
    return render_template('index.html', data=json_files)


if __name__ == '__main__':
    app.run(debug=True)