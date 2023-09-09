# Use the official Python image for Windows Server 2016
FROM python:3.7-windowsservercore-ltsc2016
COPY . /flaskpyproject
WORKDIR /flaskpyproject
RUN pip install -r requirements.txt
EXPOSE 5000
CMD [ "python", "alertapp.py" ]