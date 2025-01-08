import {logOutput} from "./lib/utils.ts";
import {UnsecuredJWT} from "jose";

const aaa = "aaa"
console.log(aaa)
logOutput("outaaa")



const produceJWT = new UnsecuredJWT()
produceJWT.setAudience("https://example.com")
produceJWT.setIssuer("https://example.com")
produceJWT.setSubject("user")
produceJWT.setExpirationTime("1h")
produceJWT.setIssuedAt()
produceJWT.setJti("123")

console.log(produceJWT.encode())




