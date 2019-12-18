# **Developer CRUD application**

I have implemented console CRUD application that has next entities:
Developer,
Skill,
Account.

Developer:
String firstName;
String lastName;
Set<Skill> skills;
Account account.

Skill:
String name.

Account:
String name;
AccountStatus (enum ACTIVE, BANNED, DELETED) status.

It use text files as a storage (in resources repository):
developers.txt, skills.txt, accounts.txt

User is able to create, read, update and delete data.

Layers:
>model - POJO classes

>view - all data that are required for user/console interaction

>controller - user’s requests handling

>repository - classes that provide access to text files

>storage

Class-chain for developer (not inheritance):
DeveloperRepository -> DeveloperController -> DeveloperView -> AppView

Class-chain for skill (not inheritance):
SkillRepository -> SkillController -> SkillView -> AppView

Class-chain for account (not inheritance):
AccountRepository -> AccountController -> AccountView -> AppView

For repository layer there are a few interfaces, such as 
GenericRepository<T,ID>, DeveloperRepository, SkillRepository, AccountRepository.
DeveloperRepository, SkillRepository, AccountRepository extend GenericRepository<T,ID>.
Classes JavaIODeveloperRepositoryImpl, JavaIOSkillRepositoryImpl, JavaIOAccountRepositoryImpl 
implement appropriate interfaces.

All basic functionality is covered with unit tests.

To start up application you should compile code (version of Java is 8) and start this with 
entry point in AppRunner class.
