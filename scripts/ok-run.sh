port=''

while getopts p: flag
do
    case "${flag}" in
	p) port="-Dserver.port=${OPTARG} ";;
    esac
done
mvn ${port}-Dspring.profiles.active=openkoda -f ../openkoda/pom.xml exec:java
