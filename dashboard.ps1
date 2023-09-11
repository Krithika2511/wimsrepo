try {
    Remove-Item -Recurse -Force .\wimsrepo\
}
catch {
  	Write-Host "There is No Wimsreport. Continue pipeline"
}
git clone https://github.com/Krithika2511/wimsrepo.git
cd .\wimsrepo\flaskpyproject
docker build -t flask-app .
docker image ls
docker ps
docker run --name flask-container --rm -d -p 5000:5000 flask-app