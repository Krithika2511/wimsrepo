FROM microsoft/aspnetcore:2.0-nanoserver-sac2016 AS base
COPY . /flask_project
WORKDIR /flask_project
RUN pip install -r requirements.txt
EXPOSE 5000
CMD [ "python", "alertapp.py" ]