## Quick Start Project for the Chat - Server

Using this project as your start code will make deploying your server (the jar-file) to Digital Ocean a "no brainer" if you follow the instructions given here

https://docs.google.com/document/d/1aE1MlsTAAYksCPpI4YZu-I_uLYqZssoobsmA-GHmiHk/edit?usp=sharing 


## Log Fil
https://docs.google.com/document/d/1Lnr9GWQ5e7Oh5jleJ12ImJhNn9d1BViUjWtDNwfOwjU/edit?usp=sharing


Vi har valgt tre klasser. Vi har en chatserver klasse som håndtere logins og connections. Og hvis det lykkes at logge ind så instansere
den en ny client, med tilhørerne datastream og tråde. Den styrer også hvornår der skrives til logfilen.

Så har vi vores ClientHandler klasse, som styrer protokolen.

Og så har vi en LogHandler der gør det muligt at skrive til vores logfil.

Vi har valgt at lave det så hver client får hver deres tråde. Og det kan være problematisk hvis der bliver overloadet af clienter. En
løsning til dette kunne være en threadpool.

Vi har alle sammen lavet det sammen. Grundet størrelsen på projektet så er det svært at uddele opgaver til hver enkelt. Ved at lave det
sammen, giver det bedre mulighed for at man forstår hele koden. 

Det er var succes fuld test, så længe begge parter overholder protokolen. Vi fandt små bugs i vores projekt og fixede dem. Ellers fungere
det rigtig godt, og var en god øvelse at prøve af.