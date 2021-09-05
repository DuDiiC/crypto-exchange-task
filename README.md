### General info

The application was created as a recruitment task to test technical skills.

### Tech-stack

- Java 16
- Spring Boot 2.5.4 with Spring Framework MVC
- Junit5 & Mockito

### Client API

As a client from which I download information about cryptocurrencies, I used [CoinRanking](https://developers.coinranking.com/api).
To run the application, create an account on the website, generate an API key and paste it in the [`application.yml`](https://github.com/DuDiiC/crypto-exchange-task/blob/master/src/main/resources/application.yml) file in the` api-key` property.

### Functionalities

Provides two endpoints:

#### 1. `GET /currencies/{currency}`

where `currency` is a cryptocurrency symbol (e.g. `BTC` for bitcoin), for which server returns a quotation list.
The `filters` parameter is available, after which you can filter in relation to which cryptocurrencies should be quoted.

example request:

    GET /currencies/BTC?filter[]=USDT&filter[]=ETH

response:

```json
{
  "source": "BTC",
  "rates": {
    "ETH": 0.0768914538,
    "USDT": 0.0000193848
  }
}
```

#### 2. `POST /currencies/exchange`

which allows you to forecast the exchange of selected currencies.

example request:

```json
{
    "from": "BTC",
    "to": [
        "ETH",
        "BTCS"
    ],
    "amount": 1
}
```

response:

```json
{
  "ETH": {
    "rate": 0.0771233623,
    "amount": 1,
    "result": 4436.5978029030,
    "fee": 509.2317029030
  },
  "from": "BTC",
  "BTCS": {
    "rate": 0.0000002312,
    "amount": 1,
    "result": 509.2317029030,
    "fee": 509.2317029030
  }
}
```
