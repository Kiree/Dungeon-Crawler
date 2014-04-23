package io.github.omn0mn0m.dungeoncrawler;

import io.github.omn0mn0m.dungeoncrawler.entity.Hero;
import io.github.omn0mn0m.dungeoncrawler.list.HostileList;
import io.github.omn0mn0m.dungeoncrawler.list.ItemList;
import io.github.omn0mn0m.dungeoncrawler.location.LocationMap;
import io.github.omn0mn0m.util.Input;
import io.github.omn0mn0m.util.NamReader;

import java.util.Random;

public class Game {

    private final int ROOMS_TO_WIN = 10;
	
    public static final NamReader namReader = new NamReader();
	public static String rootPath;
	
	public static HostileList hostileList;
    public static ItemList itemList;
    
	private Input input = new Input();
	private boolean paused = false;
	
    private Hero hero;
    private LocationMap locationMap = new LocationMap(ROOMS_TO_WIN + 1, ROOMS_TO_WIN + 1);
    public static Random random = new Random();
    
    
    public Game() {
    	if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
    		rootPath = "resources/";
    	} else {
    		rootPath = "storage/emulated/0/AppProjects/Dungeon-Crawler/resources/";
    	}
    	
    	hostileList = new HostileList();
    	itemList = new ItemList();
    	hero = new Hero();
    	
    	locationMap.createRoomAtPlayer(0, random.nextInt(itemList.getTotalItems()));
		locationMap.enterCurrentMapLocation(hero);
    }
	
    public void runInputCommand() {
    	if (!paused) {
    		switch (input.splitAndGetInput(0)) {
	            case "go":
	            	switch (input.getInputWord(1)) {
	            		case "north":
	            			checkForWin();
							locationMap.moveTo(-1, 0, hero);
							break;
						case "east":
							checkForWin();
							locationMap.moveTo(0, 1, hero);
							break;
						case "south":
							checkForWin();
							locationMap.moveTo(1, 0, hero);
							break;
						case "west":
							checkForWin();
							locationMap.moveTo(0, -1, hero);
							break;
						default:
							print("You can't go that way...");
							break;
	            	}
	                break;
	            case "look":
	            	if (input.isSplitWordTarget(1, "around")) {
	            		if (locationMap.getCurrentLocation() != null) {
	            			locationMap.printAllCurrentLocationInformation();
	            		} else {
	            			Game.print("There is nothing to see...");
	            		}
	            	} else if (input.isSplitWordTarget(1, "at")) {
						locationMap.printTargetHostileStats(input.getInputWord(2));
					}
	            	break;
	            case "attack":
	            	if (input.getSplitLength() >= 2) {
	            		hero.attack(locationMap.getHostileAtCurrentLocation(input.getInputWord(1)));
	            	} else {
	            		Game.print("You did not choose anything to attack...");
	            	}
	                break;
	            case "quit":
	                this.quit();
	                break;
	            case "restart":
	                this.restart();
	                break;
	            case "reroll":
	                hero.rerollCharacter();
	                break;
	            case "check":
	            	switch (input.getInputWord(1)) {
	            		case "stats":
	            			hero.printStats();
	            			break;
	            		case "inventory":
	            			hero.checkInventory();
	            			break;
	            		case "health":
	            			print("Health: " + hero.getStat("health"));
	            			break;
	            		default:
	            			print("That is not something valid to check...");
	            			break;
	            	}
	            	break;
	            case "drop":
	            	hero.removeItem(locationMap.getCurrentLocationItems(), input.getInputWord(1));
	            	break;
	            case "take":
	            	hero.addItem(locationMap.getCurrentLocationItems(), input.getInputWord(1));
	            	break;
	            case "pause":
	            	pause();
	            	break;
	            case "unpause":
	            	unpause();
	            	break;
	            default:
	                print("That is not a valid command");
	                break;
	        }
    	} else {
    		switch (input.splitAndGetInput(0)) {
	            case "quit":
	                this.quit();
	                break;
	            case "restart":
	                this.restart();
	                break;
	            case "reroll":
	                hero.rerollCharacter();
	                break;
	            case "pause":
	            	pause();
	            	break;
	            case "unpause":
	            	unpause();
	            	break;
	            default:
	                print("That is not a valid command");
	                break;
	        }
    	}
        
    }
    
    public void runGame() {
		for (int i = 0; i < locationMap.getHostilesInCurrentLocation(); i++) {
			if (locationMap.getHostileAtCurrentLocation(i) != null) {
				hero.takeDamage(locationMap.getHostileAtCurrentLocation(i), 0);
				locationMap.getHostileAtCurrentLocation(i).checkIfAlive(hero);
				locationMap.checkIfHostileDead(i);
			}
		}
    	hero.checkIfAlive();
    }
    
    public void heroClassSelect() {
    	hero.selectClass();
    }
    
    public void checkForWin() {
    	if (locationMap.getRoomsCleared() == ROOMS_TO_WIN) {
    		print("You walk through into the next room, but there is no more dungeon. You have reached the end. Congradulations!");
            System.exit(0);
    	}
    }
    
    public void quit() {
    	print("Are you sure you want to quit? ");
        if(input.getSimpleInput().equalsIgnoreCase("yes")) {
            System.exit(0);
        } else {
        	print("Resuming game then...");
        }
    }
    
    public void restart() {
    	print("Are you sure you want to restart?");
        if(input.getSimpleInput().equalsIgnoreCase("Yes")) {
            locationMap.resetMap();
            locationMap.resetPlayerLocation(hero);
            this.heroClassSelect();
        }
    }
    
    public void pause() {
    	paused = true;
    	print("The game is now paused.");
    }
    
    public void unpause() {
    	paused = false;
    	print("The game is resuming.");
    }
    
    public boolean isPaused() {
    	return paused;
    }
    
    public static void print(String string) {
    	System.out.println(string);
    }
}