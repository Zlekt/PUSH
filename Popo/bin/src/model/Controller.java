package model;

import java.io.IOException;
import java.util.Random;

public class Controller {

	private LinkedList gameBoard;
	
	private int rickPosition;
	private int mortyPosition;
	private Player rickPlayer;
	private Player mortyPlayer;
	private long time;
	private GameDataPlayer gameDataPlayer;
	
	public Controller() {
		gameBoard = new LinkedList(0, 0, 0);
		gameDataPlayer = new GameDataPlayer();
	}
	
	public void initializePlayers(String ricksName, String mortysName) {
		rickPlayer = new Player(ricksName,1);
		mortyPlayer = new Player(mortysName,2);
	}
	
	//INICIALZACIÓN
	public void initGameBoard(int rows, int columns, int numSeeds, int numPortals)
	{
		gameBoard = new LinkedList(rows, columns, numSeeds);
		
		for(int m = 1;m<=gameBoard.getSize(); m++) 
		{
			gameBoard.add(m);
		}
		
		int[] seeds=randomNumberToSeeds(numSeeds);
		for(int i=0; i<seeds.length; i++)
		{
			System.out.print(seeds[i]+" ");
		}
		for(int i=0; i<seeds.length; i++)
		{
			gameBoard.searchNode(seeds[i]).setHasSeed(true);
		}
		
		createPortals(numPortals);
		rickPosition=ubicationRick();
		mortyPosition=ubicationMorty();
		setRickMorty();

	}
	//SEMILLAS
	private int[] randomNumberToSeeds(int numSeeds) { 
		int c=0;
		int[] seeds=new int[numSeeds];
		for(int i=0; i<numSeeds; i++)
		{
			seeds[i]=-1;
		}
		Random randomNum = new Random();
		if(seeds.length==1)
		{
			int num = randomNum.nextInt(gameBoard.getSize())+1;
			seeds[0]=num;
		}
		while(c<numSeeds-1)
		{
			int num = randomNum.nextInt(gameBoard.getSize())+1;
			if(seeds[0]==-1)
			{
				seeds[0]=num;
			}
			else
			{
				int a=0;
				boolean repeated=false;
				while(seeds[a]!=-1)
				{
					if(num==seeds[a])
					{
						repeated=true;
					}
					a++;
				}
				if(repeated==false)
				{
					seeds[a]=num;
					c++;
				}
			}
		}
		return seeds;
	}
	
	
	//PORTALES
	private void createPortals(int numPortals)
	{
		int[] portalNodes=new int[numPortals*2];
		
		for(int i=0; i<portalNodes.length; i++)
		{
			portalNodes[i]=-1; //llena el arreglo donde se guardaran las posiciones de los portales de -1 para saber que están vacios
		}
		
		int c=0;
		Random randomNum = new Random();
		
		while(c<numPortals*2)
		{
			boolean repeated=false;
			int num = randomNum.nextInt(gameBoard.getSize())+1;
			for(int i=0; i<portalNodes.length; i++)//verifica si el valora aleatorio ya está. 
			{
				if(num==portalNodes[i])
				{
					repeated=true;
					break;
				}
			}
			if(repeated==false)
			{
				portalNodes[c]=num;
				c++;
			}
		}
		
		for(int i=1; i<portalNodes.length; i+=2)//cada casilla se enlaza con la anterior por eso el paso es 2.
		{
			gameBoard.searchNode(portalNodes[i]).setConnected(gameBoard.searchNode(portalNodes[i-1]));
			gameBoard.searchNode(portalNodes[i-1]).setConnected(gameBoard.searchNode(portalNodes[i]));
		}
		
	}
	
	
	//GENERACIÓN DE UBICACIÓN RICK & MORTY
		public int ubicationRick() {
			
			Random ricksPos = new Random();
			int position = ricksPos.nextInt(gameBoard.getSize())+1;
			return position;
		}
		public int ubicationMorty() {
			
			Random mortysPos = new Random();
			int position = mortysPos.nextInt(gameBoard.getSize())+1;
			return position;
		}
		public void setRickMorty()
		{
			gameBoard.searchNode(rickPosition).setHasRick(true);
			gameBoard.searchNode(mortyPosition).setHasMorty(true);
		}
	
	
	//MÉTODO SIN PROPÓSITO
	private int generateRandomNumber(int min, int max) {
		int num = (int) Math.floor(Math.random() * (min - max + 1) + max);
		return num;
	}
	
	//LANZAMIENTO DE DADOS
	public int rollDice() {
			
		Random dice = new Random();
		int diceResult = dice.nextInt(6)+1;
		return diceResult;
	}
		
	
	//MOVIMIENTO RICK Y MORTY
public void moveRick(int diceResult, boolean moveForward) {
		
		int initialPos = gameBoard.getRicksNode().getValue();
		gameBoard.getRicksNode().setRick(false);
		int finalPos = 0;
		
		if(moveForward) {
			
			finalPos = initialPos+diceResult;
			
			if(finalPos>gameBoard.getSize()) {
				finalPos = finalPos-gameBoard.getSize();
			}
			
		}
		else {
			
			finalPos = initialPos-diceResult;
			
			if(finalPos<1) {
				int distanceToFirst = initialPos-1;
				int restOfMove = diceResult-distanceToFirst;
				finalPos = (gameBoard.getSize()+1)-restOfMove;
			}
			
		}
		gameBoard.searchNode(finalPos).setRick(true);
		
		if(gameBoard.searchNode(finalPos).getHasSeed()) {
			rickPlayer.collectSeed();
			gameBoard.searchNode(finalPos).setHasSeed(false);
		}
		if(gameBoard.searchNode(finalPos).getConnected()!=null) {
			
			gameBoard.searchNode(finalPos).teleportRick();
			
			if(gameBoard.getRicksNode().getHasSeed()) {
				
				rickPlayer.collectSeed();
				gameBoard.searchNode(finalPos).setHasSeed(false);
			}
		}
	}
	
	public void moveMorty(int diceResult, boolean moveForward) {
		
		int initialPos = gameBoard.getMortysNode().getValue();
		gameBoard.getMortysNode().setMorty(false);
		int finalPos = 0;
		
		if(moveForward) {
			
			finalPos = initialPos+diceResult;
			
			if(finalPos>gameBoard.getSize()) {
				finalPos = finalPos-gameBoard.getSize();
			}
			
		}
		else {
			
			finalPos = initialPos-diceResult;
			
			if(finalPos<1) {
				int distanceToFirst = initialPos-1;
				int restOfMove = diceResult-distanceToFirst;
				finalPos = (gameBoard.getSize()+1)-restOfMove;
			}
			
		}
		gameBoard.searchNode(finalPos).setMorty(true);
		
		if(gameBoard.searchNode(finalPos).getHasSeed()) {
			
			mortyPlayer.collectSeed();
		}
		if(gameBoard.searchNode(finalPos).getConnected()!=null) {
			
			gameBoard.searchNode(finalPos).teleportMorty(); 
			
			if(gameBoard.getMortysNode().getHasSeed()) {
				
				mortyPlayer.collectSeed();
			}
		}
	}

	
	//TO STRING
	
	public String toString()
	{
		return gameBoard.toString();
		
	}
	
	public String printGameBoard() {
		String board = gameBoard.toString();
		return board;
	}
	
	
	public LinkedList getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(LinkedList gameBoard) {
		this.gameBoard = gameBoard;
	}
	
	//PORTAL TO STRING
		public String portalToString()
		{
			return gameBoard.portalToString();
		}
	
	//CHECK GAME_END
	public boolean executeGame() {
		
		return gameBoard.countSeeds();
	}
	
	//END GAME
	public String showTop5() {
		
		return gameDataPlayer.printTop5();
	}
			
	public int findPlayerOnSavedData(Player p) {
		if(gameDataPlayer.playerPosition(p.getName()) != -1) {
			return gameDataPlayer.playerPosition(p.getName()) ;
		}
			
			return -1;
	}
			
	public void doSerializar() throws IOException{
		gameDataPlayer.serialize();	
	}
		
	public void doDeserializar() throws ClassNotFoundException, IOException {
		gameDataPlayer.deserialize();
	}
			
	public void addWinner() {
		if(rickPlayer.getScore()>mortyPlayer.getScore()) {
			
			gameDataPlayer.addPlayerToData(rickPlayer);
		}
		else {
			gameDataPlayer.addPlayerToData(mortyPlayer);
		}
	}

}

