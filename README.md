# Agenda de Usuários — trabalho CRUD

Aplicativo Android em Java com interface XML e banco SQLite local. Os dados continuam salvos ao fechar o aplicativo. Não precisa de internet.

## Como demonstrar

1. Digite um nome e toque em **Gravar**. O banco gera um código único.
2. Deixe o código vazio e toque em **Consultar** para listar todos.
3. Informe um código e toque em **Consultar** para carregar o nome.
4. Edite o nome e toque em **Atualizar**, mantendo o código do usuário.
5. Toque em **Deletar** e confirme a exclusão. Cancelar preserva o cadastro.
6. Feche e abra o aplicativo e consulte novamente para demonstrar a persistência.

Gravar sempre cria um novo registro, mesmo que o campo código esteja preenchido. Nomes iguais são permitidos: atualização e exclusão usam o código para afetar somente um registro. Nomes vazios e códigos inválidos são rejeitados.

## Organização

- `app/src/main/res/layout/activity_main.xml`: tela com os quatro botões, campos e resultados. O atributo correto de negrito é `android:textStyle="bold"`.
- `app/src/main/java/com/example/bancodedados/MainActivity.java`: eventos dos botões, validações, consulta, mensagens e confirmação.
- `app/src/main/java/com/example/bancodedados/BancoHelper.java`: criação da tabela e operações SQL parametrizadas. Usa `SQLiteOpenHelper`, `ContentValues` e fecha os cursores após consultar.
- `app/src/androidTest/java/com/example/bancodedados/BancoHelperTest.java`: testes do ciclo CRUD, nomes iguais, apóstrofos, persistência, registro inexistente e nome vazio. Usa banco separado dos dados reais.

## Executar

Abra o projeto no Android Studio, aguarde a sincronização do Gradle e execute o módulo `app` em um emulador ou celular Android (API 24 ou superior).

Para compilar: `gradlew.bat assembleDebug`. Para análise estática: `gradlew.bat lintDebug`. Com dispositivo conectado: `gradlew.bat connectedDebugAndroidTest`.
