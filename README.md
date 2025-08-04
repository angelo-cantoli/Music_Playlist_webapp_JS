
# Esercizio 2: Playlist Musicale (Versione JavaScript)

## Descrizione

Questa applicazione web consente la gestione personale di playlist di brani musicali. Ogni utente può caricare i propri brani, organizzarli in playlist e ascoltarli tramite un player integrato. L'applicazione è realizzata in modalità single-page (SPA) utilizzando JavaScript per la gestione asincrona delle interazioni.

## Funzionalità principali

- **Gestione utenti**: ogni utente ha username, password, nome e cognome. Brani e playlist sono personali e non condivisi.
- **Gestione brani**: ogni brano contiene titolo, immagine, titolo dell’album, interprete (singolo o gruppo), anno di pubblicazione, genere musicale (prefissato) e file musicale. Un brano può appartenere a un solo album.
- **Gestione playlist**: una playlist è composta da brani scelti tra quelli caricati dall’utente. Ogni playlist ha titolo, data di creazione ed è associata al suo creatore. Lo stesso brano può essere inserito in più playlist.

## Flusso dell'applicazione

1. **Login**: accesso tramite username e password.
2. **Home Page**:
   - Elenco delle proprie playlist, ordinate per data di creazione decrescente.
   - Form per caricare un nuovo brano.
   - Form per creare una nuova playlist, con elenco dei propri brani ordinati per autore (A-Z) e anno di pubblicazione (crescente). Possibilità di selezionare più brani.
3. **Playlist Page**:
   - Visualizzazione dei brani della playlist in una tabella (1 riga, 5 colonne), con titolo e immagine dell’album.
   - Ordinamento dei brani per autore (A-Z) e anno di pubblicazione (crescente).
   - Navigazione tra gruppi di 5 brani tramite bottoni "PRECEDENTI" e "SUCCESSIVI" (gestita lato client).
   - Form per aggiungere nuovi brani alla playlist, con selezione multipla tra i propri brani non già presenti.
4. **Player Page**:
   - Visualizzazione dettagliata di un brano selezionato e player audio per la riproduzione.

## Specifiche JavaScript

- Dopo il login, l’intera applicazione è una single-page: tutte le interazioni avvengono senza ricaricare la pagina.
- Le operazioni che modificano i dati (creazione brani/playlist, aggiunta brani, salvataggio ordinamento) avvengono tramite chiamate asincrone al server.
- La navigazione tra blocchi di brani nelle playlist (PRECEDENTI/SUCCESSIVI) è gestita completamente lato client.
- **Riordino playlist**: dalla Home, ogni playlist ha un link per accedere a una finestra modale di riordino. L’utente può trascinare i brani per cambiare l’ordine (drag & drop, solo lato client). Il nuovo ordinamento viene salvato sul server solo al click su "salva ordinamento". Ai successivi accessi, l’ordinamento personalizzato viene mantenuto. Un brano aggiunto a una playlist con ordinamento personalizzato viene inserito in ultima posizione.

## Requisiti

- Node.js e un database relazionale (es. MySQL, PostgreSQL).
- Browser moderno con supporto JavaScript ES6.

## Avvio del progetto

1. Installare le dipendenze (`npm install`).
2. Configurare il database secondo lo schema fornito.
3. Avviare il server (`npm start` o comando equivalente).
4. Accedere tramite browser all’indirizzo indicato dal server.

## Note

- Tutte le funzionalità sono accessibili solo dopo il login.
- L’applicazione non supporta la condivisione di brani o playlist tra utenti.
- I generi musicali sono prefissati e non modificabili dall’utente.

