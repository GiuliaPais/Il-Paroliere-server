package uninsubria.server.dbpopulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum wordsOfGrid {

    //+STIA FINE FINTA LISTA SLIP TISI PISTA TINTA
    // -PENTITI ISTINTI INTIMI LIMITI PETI
    GRID0(new String[]{"I", "T", "E", "U", "I", "N", "P", "H", "F", "T", "M", "I", "A", "I", "S", "L"}, "STIA","FINE","FINTA","LISTA","SLIP","TISI","PISTA","TINTA",
            "PENTITI","ISTINTI","INTIMI","LIMITI","PETI"),
    //+DELEGA DAGA MAGO MAGA LEGA GEL AGO DAMA MODA AEDO ODE
    // -OMEGA LEGO EGO AMI AMAI DEA LEGAMI
    GRID1(new String[]{"M", "O", "E", "L", "E", "G", "D", "E", "H", "A", "A", "A", "U", "U", "I", "M"}, "DELEGA","DAGA","MAGO","MAGA","LEGA","GEL","AGO","DAMA","MODA","AEDO","ODE",
            "OMEGA","LEGO","EGO","AMI","AMAI","DEA","LEGAMI"),
    //+MANSARDA MANDARE MANDATA DURATA STRADA ANDARE ANDATA DATARE MANATA AMANTE SATANA ANSARE RASATA MADRE TENDA STARE RUDE DAMA DARE DUE RADA DATA MANE TANA ANTA ANSA AURA ERTA RATA
    //+ASTA ARTE TESA EST ERA ASTA ARTE
    // -SETA TANA MANTRA SATANA STRADE ANATRA DATA TRE RATA SARDA
    GRID2(new String[]{"M", "A", "U", "E", "A", "A", "D", "R", "U", "N", "T", "A", "H", "E", "S", "A"}, "MANSARDA","MANDARE","MANDATA","DURATA","STRADA","ANDARE","ANDATA","DATARE","MANATA","AMANTE","SATANA","ANSARE","RASATA","MADRE","TENDA","STARE","RUDE","DAMA","DARE","DUE","RADA",
            "DATA","MANE","TANA","ANTA","ANSA","AURA","ERTA","RATA","ASTA","ARTE","TESA","EST","ERA","ASTA","ARTE","SETA","TANA","MANTRA","SATANA","STRADE","ANATRA","DATA","TRE","RATA","SARDA"),
    //+AIUOLA PALCO PIANA PIANO ALBO PALO CLAN BOIA LANA PAIO CONO PIO NAIA ANO
    // -ALPI PUOI PAIA POI CON ANA NOA
    GRID3(new String[]{"U", "I", "A", "I", "O", "P", "A", "A", "B", "L", "N", "O", "G", "H", "C", "O"}, "AIUOLA","PALCO","PIANA","PIANO","ALBO","PALO","CLAN","BOIA","LANA","PAIO","CONO","PIO","NAIA","ANO",
            "ALPI","PUOI","PAIA","POI","CON","ANA","NOA"),
    //SCIARADA NASCITA CALARSI CIALDA FASCIA BASTIA LANCIA CIARLA FALDA TARSIA DARSI BAITA SCALA CASTA TASCA FLAN TIARA ASCIA RADA FAN BAIA CALA SALA NAIA TARA CASA
    //ASTA SCIA STIA ALA TIC ARA
    // -DRASTICA BUFALAI FALSATI BUFALA SLANCIA SCALDA MALATI LASCIA SALATI SFUMA CASATI LADRA CALDA FALSA FASCI ISLAM
    GRID4(new String[]{"T", "A", "R", "A", "I", "S", "L", "D", "C", "A", "F", "A", "N", "B", "U", "M"}, "SCIARADA","NASCITA","CALARSI","CIALDA","FASCIA","BASTIA","LANCIA","CIARLA","FALDA","TARSIA","DARSI",
            "BAITA","SCALA","CASTA","TASCA","FLAN","TIARA","ASCIA","RADA","FAN","BAIA","CALA","SALA","NAIA","TARA","CASA","ASTA","SCIA","STIA","ALA","TIC","ARA",
            "DRASTICA","BUFALAI","FALSATI","BUFALA","SLANCIA","SCALDA","MALATI","LASCIA","SALATI","SFUMA","CASATI","LADRA","CALDA","FALSA","FASCI","ISLAM"),
    //CHIOSARE COSTARE CHIOSA DOSARE DOTARE NITORE OSTARE TOSARE SANTO MORSA CORSA STARE ROSTA COSTA RESTO OSARE REATO DOSE MOTA MORA NATO
    //NASO COSA ARSO ROSA RASO RESA SERA EST ORA ERA
    // - OSTINARE COSTINA COREANI DORATI CORANI MORSE SANTI COSTI RESTA REATI RASOI COREA RHO CHI
    GRID5(new String[]{"I", "L", "B", "R", "N", "T", "M", "D", "A", "S", "O", "C", "E", "R", "H", "I"}, "CHIOSARE","COSTARE","CHIOSA","DOSARE","DOTARE","NITORE","OSTARE","TOSARE","SANTO","MORSA","CORSA","STARE","ROSTA",
            "COSTA","RESTO","OSARE","REATO","DOSE","MOTA","MORA","NATO","NASO","COSA","ARSO","ROSA","RASO","RESA","SERA","EST","ORA","ERA",
            "OSTINARE","COSTINA","COREANI","DORATI","CORANI","MORSE","SANTI","COSTI","RESTA","REATI","RASOI","COREA","RHO","CHI"),
    //+REGIME FINE RENI TIR
    // -NERI  NEI FINGE FINGI REGNI TINGE GENI MIE
    GRID6(new String[]{"F", "T", "M", "E", "R", "T", "B", "I", "U", "F", "I", "G", "U", "R", "E", "N"}, "REGIME","FINE","RENI","TIR","NERI","NEI","FINGE","FINGI","REGNI","TINGE","GENI","MIE"),
    //+METTERE TEMERE ACUME TETTA ETERE TBC RETE TECA
    // -NUTRE TUTTA RUTTA METTE CATTURE TEMETE
    GRID7(new String[]{"Z", "U", "A", "U", "M", "E", "C", "T", "E", "T", "T", "B", "E", "R", "U", "N"}, "METTERE","TEMERE","ACUME","TETTA","ETERE","TBC","RETE","TECA",
            "NUTRE","TUTTA","RUTTA","METTE","CATTURE","TEMETE"),
    //+OLEOSO FAUNO DOSSO ESODO LEONE SESSO ESOSO OSSEO NUDO FUNE LODO NODO DOLO DOSE SODO DUE FAN NOLO LENA SOLO UNO ASSO ASSE OSSO ANO NEO
    // -LODASSE ASSOLSE ASSOLO DOLOSA DUNA DUNE NUDA SOLE AFA ESSA ESSO
    GRID8(new String[]{"E", "S", "L", "O", "S", "O", "O", "E", "S", "D", "A", "N", "A", "F", "U", "E"}, "OLEOSO","FAUNO","DOSSO","ESODO","LEONE","SESSO","ESOSO","OSSEO","NUDO","FUNE","LODO","NODO","DOLO","DOSE",
            "SODO","DUE","FAN","NOLO","LENA","SOLO","UNO","ASSO","ASSE","OSSO","ANO","NEO","LODASSE","ASSOLSE","ASSOLO","DOLOSA","DUNA","DUNE","NUDA","SOLE","AFA","ESSA","ESSO"),
    //+SPANNA CANAPO POSATA PANNA NASPO SPOSA PASSO BASSO CANNA NASSA TASSO TASSA CASSA POSA ASPO TANA NASO ANSA ANTA OSSO RATA RASO ASSO CASO CASA
    // -SPOSATA OSANNA TAPPO BANNA POSSO SANTA CANTA POP BOSS NANA NASA RANA NATA OSSA
    GRID9(new String[]{"O", "B", "H", "C", "V", "S", "A", "T", "P", "S", "A", "N", "O", "P", "N", "R"}, "SPANNA","CANAPO","POSATA","PANNA","NASPO","SPOSA","PASSO","BASSO","CANNA","NASSA","TASSO","TASSA","CASSA","POSA","ASPO","TANA",
            "NASO","ANSA","ANTA","OSSO","RATA","RASO","ASSO","CASO","CASA","SPOSATA","OSANNA","TAPPO","BANNA","POSSO","SANTA","CANTA","POP","BOSS","NANA","NASA","RANA","NATA","OSSA"),
    //POETA NOVA VANO VETO VOTO VATE RIPA TIPO TOP NOTO NOTA EVO AVO NATO RITO IRTO TOT ATEO TIR ANO
    // -POTEVANO POTAVO POTEVO POTEVA NOVE VOTI TOPI
    GRID10(new String[]{"N", "O", "E", "I", "V", "A", "T", "I", "E", "O", "P", "R", "A", "T", "H", "R"}, "POETA","NOVA","VANO","VETO","VOTO","VATE","RIPA","TIPO","TOP","NOTO","NOTA","EVO","AVO","NATO","RITO","IRTO","TOT","ATEO","TIR",
            "ANO","POTEVANO","POTAVO","POTEVO","POTEVA","NOVE","VOTI","TOPI"),
    //+ BEVUTA RUDERE BREVE BARBA AVERE EREDE BEARE ETERE ETERA RUDE BERE VATE ERBA DUE TABE ERTA RETE ARTE ERA
    // -AVREBBE AVRETE AUREA VEDE VERE UNTA UVA
    GRID11(new String[]{"N", "R", "E", "D", "U", "T", "E", "U", "A", "V", "R", "A", "E", "B", "B", "E"}, "BEVUTA","RUDERE","BREVE","BARBA","AVERE","EREDE","BEARE","ETERE","ETERA","RUDE","BERE","VATE","ERBA","DUE","TABE",
            "ERTA","RETE","ARTE","ERA","AVREBBE","AVRETE","AUREA","VEDE","VERE","UNTA","UVA"),
    //+SINCERO, INFERO, RESINA, SPINA, PERSO,SIERO,FINE, BICI,FECI,SPIA,PESO, NERO, RENI, CERO, NEO, REO
    GRID12(new String[]{"A", "I", "S", "O", "N", "P", "E", "R", "B", "C", "F", "N", "I", "M", "I", "M"}, "SPANCERO","SPANIERO","CINESINA","INCINERO","PANCINE","FRENICI","ROSPINA","PERNICI","NEFROSI","INFERSI","OREFICI","SPAIERO","SINCERO",
                   "CINESIA","SPANCI","FENICI","PENICI","INFERO","SPIERO","ORFICI","APERSI","FRESIA","MINERO","MICINE","CINESI","RESINA","ORSINA","NICESI","INCESO","INCERO","ESORNI",
                   "ERNICI","ROSINA","EOSINA","CEROSI","NIMBI","FRENI","PANCE","SPINA","PINCE","PERNI","SPANI","PIENI","CESPI","ROSPI","PANIE","ROSPE","ROSPA","PERSI","CIFRO","PERSO",
                   "CIFRE","SPERO","FRESO","FRESI","SPECI","PIERO","ESPIA","CERNI","EROSI","SIERO","ROSEI","CESIA","SPIN","FINE","FIMI","PANI","NIFE","PENI","NEPA","ENFI","REPS","NAPI",
                   "PINA","CEFI","FEN","NIP","BICI","SPIE","FECI","REFI","PECI","SPIA","PESO","PESI","SPAI","PERO","PIN","MINE","FM","CIF","MIMI","SPA","PSI","ESP","PER","SPI","PS","CINE",
                   "PEI","NERO","PIE","RENI","CENI","CIMI","ORNI","PC","SENI","ANCE","PIA","MICE","MICI","APE","IFE","API","EPA","FI","ORSE","ORSI","SORE","ERSI","BI","CIN","RESO","CESI",
                   "CERO","NAIE","RESI","EROS","ROSI","ROSE","INA","SOR","NEO","NEI","ANI","INE","OECI","ROS","IMI","ROE","ERO","MI","ISO","NE","IN","ICI","OSI","ORE","REO",
                    "REI","SEI","SIE","SIA","OSE","AIE","EIA");

    private final String[] grid;
    private final String[] words;

    wordsOfGrid(String[] grid, String ... words) {
        this.grid = grid;
        this.words = words;
    }

    public String[] getGrid() {
        return grid;
    }

    public List<String> getWords() {
        List<String> words = new ArrayList<>();
        for(String x: this.words)
            words.add(x);
        return words;
    }

    public String getRandomWord(wordsOfGrid wog){
        String word;
        Random random = new Random();
        List<String> words = wog.getWords();
        word = words.get(random.nextInt(wog.getWords().size()));
        return word;
    }

    public static wordsOfGrid getRandomGrid(){
        wordsOfGrid wog;
        Random rdm = new Random();
        int index = rdm.nextInt(wordsOfGrid.values().length);
        System.out.println("Griglia N " + index);
        wog = switch (index) {
            case (0) -> GRID0;
            case (1) -> GRID1;
            case (2) -> GRID2;
            case (3) -> GRID3;
            case (4) -> GRID4;
            case (5) -> GRID5;
            case (6) -> GRID6;
            case (7) -> GRID7;
            case (8) -> GRID8;
            case (9) -> GRID9;
            case (10) -> GRID10;
            case (11) -> GRID11;
            // si aggiunga una linea qui in caso si voglia aggiungere altre griglie
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };

        return wog;
    }

    @Override
    public String toString() {
        return "wordsOfGrid{" +
                "grid='" + grid + '\'' +
                ", words='" + words + '\'' +
                '}';
    }
}
