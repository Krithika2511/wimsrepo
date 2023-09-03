FROM python:3.11
COPY . /flask_project
WORKDIR /flask_project
RUN pip install -r requirements.txt
EXPOSE 5000
CMD [ "python", "alertapp.py" ]