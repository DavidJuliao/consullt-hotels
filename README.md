# consullt-hotels
Api criada para consumir um broker com dois endoints que contem informações de hotéis,
filtrar as informações capturando os valores de adultos e crianças e realizar calculo de comissão.


### 📋 Pré-requisitos

De que coisas você precisa para instalar o software e como instalá-lo?

Instalar o Redis para que a funcionalidade de chache funcione, pode fazê-lo seguindo o link - [Instalação Redis](https://medium.com/@prog.tiago/redis-instalando-via-docker-58cb1d2cfb3b)

## 🛠️ Construído com


* Spring Boot
* Spring Data
* Spring Security
* Spring Cloud (OpenFeign)
* Maven
* Jjwt
* Lombok
* Mapstruct
* Swagger 3
* Redis

## 🚀 Começando

Essas instruções permitirão que você obtenha uma cópia do projeto em operação na sua máquina local para fins de desenvolvimento e teste.

Para conseguir o Authorization para ter acesso ao principal endpoint terá que conseguir um token que 
é gerado no path:
```
/auth/google
```
que é um endpoint que simula um login com google, neste tipo de login 
o front envia um id_token fornecido pela [Api do google](https://developers.google.com/oauthplayground/)
na lista localizada do lado esquerdo com o titulo "Step 1 Select & authorize APIs"
procure a opção "Google OAuth2 API v2" e selecione todos os subitens e clice em  Authorize APIs.
Em seguida insira uma conta válida do gmail e depois clique em "Exchange authorization code for tokens".
Do lado direito será gerada uma resposta de requisição com status 200 se tudo ocorrer ok,
copie o conteúdo do campo "id_token" , ele é o parâmetro "googleTokenId" usado no [POST] /auth/google.

Realizado o post em /auth/google utilizando será retornado um json com algumas informações,o campo 
"jwtToken" é o Bearer JWT requerido para poder realizar uma requisição nos demais endpoits.


O endpoint 
```
/hotel-reservation/city/{cityId}
```
retorna as informações de hotéis por cidade, recebendo o pararâmetro "cityId" que é o identificador da cidade.

O endpoint 
```
/hotel-reservation/hotel/{hotelId}
```
retorna as informações de hotéis por Hotel, recebendo o pararâmetro "hotelId" que é o identificador da hotel.
Além disso ele é o responsál por realizar o calculo de comissão descrita nos passos:
1. Pegar a quantidade de dias e multiplicar pelo valor do Adulto
2. Pegar a quantidade de dias e multiplicar pelo valor do Criança
3. Adicionar a comissão de .70 para adulto e para criança
Exemplo de formula para fazer isso ({valorTotalAdulto}+{valorTotalCriança}/0.7)
Ex: (500/0.7);
4. Somar tudo e você terá o totalPrice

