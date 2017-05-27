#Introduction 
Projektas: **Survey Application** - internetinių apklausų rinkimo sistema. 

Pagrindinis sistemos planuojamas funkcionalumas:
- Naudotojų valdymas
- Apklausų kūrimas
- Apklausų atsakymo surinkimas
- Ataskaitų sudarymas
- Apklausos ir atsakymų importavimas iš Excel failo

#Getting Started
- Pagrindinė IDE: **IntelliJ IDEA Ultimate Edition** - galite atsisiųsti iš https://www.jetbrains.com/idea/download/
- Versijavimo valdymo sistema: **Git** - https://git-scm.com/download/win
- Build įrankis - **Maven**.

Projekto parsisiuntimas:
- Atidaryti IntelliJ
- Paspausti *Check out from version control* > *Git*
- Į *Git repository URL* įvesti https://javainis.visualstudio.com/_git/SurveyApplication
- Prisijunkite su savo Microsoft paskyra
- PgAdmin: Create database, pavadinimas: SurveyDB
- Įvykdyti SQL iš projekto: resources/postgresql/createdb.sql
- IntelliJ IDEA: Databases: Datasource -> PostgreSQL
- Database: SurveyDB
- User: postgres, password: ****
- add tomee server and add SurveyApplication:war exploded artifact.
- try building, if you fail open maven projects refresh and edit your added artifact by adding maven lib's.