Remove-Item -Recurse -Force .\wimsrepo\
git clone https://github.com/Krithika2511/wimsrepo.git
cd .\wimsrepo\flaskpyproject\
Remove-Item -Recurse -Force .\metric-collection\
cp -r C:\ProgramData\Jenkins\.jenkins\workspace\metric-collection
git add .
$emailid = $env:EMAILID
$username = $env:USERNAME
git config --global user.email $emailid
git config --global user.name $username
git commit -m "Push to Git"
git remote -v
$accessToken = $env:ACCESS_TOKEN
$gitUrl = "https://$accessToken@github.com/Krithika2511/wimsrepo.git"
git remote set-url origin $gitUrl
git push --set-upstream origin master