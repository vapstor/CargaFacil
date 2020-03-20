<h1 align="center">
  CargaFacil
</h1>

<p align="center">
  <strong>Repositório para centralizar o Ambiente de Desenvolvimento</strong>
  <p align="center">
    <img src="https://ci.appveyor.com/api/projects/status/g8d58ipi3auqdtrk/branch/master?svg=true" alt="Config. Device Activity Passing." />
     <!--<img src="https://ci.appveyor.com/api/projects/status/216h1g17b8ir009t?svg=true" alt="Config. Device Activity Crashing." /> -->
    <img src="https://img.shields.io/badge/version-1.11.2-blue.svg" alt="Current APP version." />  
  </p>
</p>

## 📋 Briefing

  Aplicativo para emitir notas não fiscais através de balanças bluetooth.


## 📖 Requirements
```
   implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation "androidx.transition:transition:1.3.1"
    implementation "androidx.drawerlayout:drawerlayout:1.1.0-alpha04"
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    testImplementation 'junit:junit:4.12'
    implementation 'com.google.android.material:material:1.1.0'

```

## 🚀 ScreensShots
<div style="float: left">
  <img src="app/src/main/res/screenshots/screen3.png?raw=true" width="250"/>
  <img src="app/src/main/res/screenshots/screen2.png?raw=true" width="250"/> 
  <img src="app/src/main/res/screenshots/screen1.png?raw=true" width="250"/> 
</div>

## 👏 Todo (Desenvolvimento)

- [x] Criar repositório no Github
- [x] Criar SplashScreen
- [x] Utilizar Fundamentos do Material Design
- [ ] Realizar Conexão Via Bluetooth à impressora

* Desenvolver HomePage ["HOME"]

  -  [ ] ~Login~
  -  [x] Implementar Menu Lateral
  -  [x] Implementar TextInputsLayouts e TextViews
  -  [x] Implementar Fade para revelar/esconder informações da empresa
  -  [x] Implementar Botões
  -  [ ] Implementar CheckBox "Salvar Placa" - savedInstanceState
  -  [x] Configurar TextViews Dinâmicas da Empresa com SharedPreferences
  -  [x] Configurar cálculo para valores do Input e consequentemente renderização
  -  [x] Configurar botões de Menu:
        - [x] Iniciar Nova Pesagem
        - [ ] Calculadora
        - [ ] Histórico
        - [x] Administrativo
      
* Desenvolver Tela de Iniciar Nova Pesagem
  - [x] Implementar inputs e validações
  - [x] Cálculo de Pesagem Líquida
  - [ ] Cálculo de Volume

* Desenvolver Tela de Conexão à Impressora
  - [ ] Interface Bluetooth Serial
  - [ ] Auto-Conexão [com pin '1234']
  - [ ] Conexão ao Dispositivo
  
* Desenvolver Tela Calculadora
  - [ ] Implementar TextInputsLayouts para previsão de cálculo
  - [ ] Configurar Inputs para cálculo sair correto
  
* Desenvolver Tela  de Histórico
  - [ ] Implementar RecyclerView com Model de Pesagem
  - [ ] Implementar PopUp com detalhes da Pesagem
  - [ ] Implementar Função Excluir com Swipe
  
* Desenvolver Tela Administrativa
  - [x] Implementar tela de login com Senha
  - [ ] Validar tela de login
  - [x] Implementar TextLayoutsInputs Para Edição dos Dados da Empresa
  - [x] Validar e Configurar novos dados em SharedPreferences

## How to version

Versionamento será dividido entre

- Mudanças significativas de funcionalidade do App (+x.0.0)
- Adição de novas funcionalidades (0.+x.0)
- Ajustes de Bugs (0.0.+x)

#### Exemplo:

> Foram adicionadas 3 novas telas, 5 novas funcionalidades e corrigidos 15 bugs. Logo a versão continuará 1, porém com 8 incrementos de funcionalidades e 15 correções de bugs. Versão final: 1.8.15

#### 👏 Todo (README.MD)

- [x] Implementar ScreensShots no README.MD
- [x] Adicionar Dependências
- [x] Incrementar Todo(Dev)


