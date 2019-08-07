echo "CREATE ACCOUNTS"
curl -s -w "\n" -H "Content-Type: application/json" --request POST --data '{"user":"test1","amount":"100"}' http://localhost:8080/api/accounts;
curl -s -w "\n" -H "Content-Type: application/json" --request POST --data '{"user":"test2","amount":"100"}' http://localhost:8080/api/accounts;
echo "REQUEST TRANSFERS"
curl -s -w "\n" -H "Content-Type: application/json" --request POST --data '{"idSourceAccount":"1", "idDestinationAccount":"2", "amount":"100"}' http://localhost:8080/api/transfers;
curl -s -w "\n" -H "Content-Type: application/json" --request POST --data '{"idSourceAccount":"1", "idDestinationAccount":"2", "amount":"100"}' http://localhost:8080/api/transfers;
echo "ACCOUNTS state"
curl -s -w "\n" -H "Content-Type: application/json" --request GET http://localhost:8080/api/accounts/1;
curl -s -w "\n" -H "Content-Type: application/json" --request GET http://localhost:8080/api/accounts/2;
echo "TRANSFERS state"
curl -s -w "\n" -H "Content-Type: application/json" --request GET http://localhost:8080/api/transfers/1;
curl -s -w "\n" -H "Content-Type: application/json" --request GET http://localhost:8080/api/transfers/2;
