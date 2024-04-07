package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
//import net.botwithus.api.game.hud.inventories.BackpackInventory;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.impl.ChatMessageEvent;
import net.botwithus.rs3.game.Client;
//import net.botwithus.rs3.game.hud.interfaces.Interfaces;
//import net.botwithus.rs3.game.js5.types.NpcType;
//import net.botwithus.rs3.game.queries.builders.animations.ProjectileQuery;
import net.botwithus.rs3.game.Item;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Component;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.components.ComponentQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;
//import net.botwithus.rs3.events.impl.SkillUpdateEvent;
//import net.botwithus.rs3.game.skills.Skills;
//import net.botwithus.rs3.game.*;
//import net.botwithus.rs3.game.actionbar.ActionBar;
//import net.botwithus.rs3.events.EventBus;

import java.util.Random;
import java.util.concurrent.Callable;
//import java.util.function.Supplier;

public class SkeletonScript extends LoopingScript {

    private BotState botState = BotState.IDLE;
    private Random random = new Random();


    /////////////////////////////////////Botstate//////////////////////////
    enum BotState {
        //define your own states here
        IDLE,
        INBETWEEN_EVENTS,
        EVIL_TREE_NURTURE,
        EVIL_TREE_CHOPPING,
        EVIL_TREE_KINDLING,
        INFERNAL_STAR_MINE,
        INFERNAL_STAR_PYREFIENDS

        //...
    }



    /////////////////////////////////////ChatMessage Stunned + No Food//////////////////////////
    public SkeletonScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new SkeletonScriptGraphicsContext(getConsole(), this);

        // Subscribe to ChatMessageEvent
        subscribe(ChatMessageEvent.class, chatMessageEvent -> {
            // Idles when stunned
            if (chatMessageEvent.getMessage().contains("You've been prevented from moving")) {
                Execution.delay(4000); // Idle for 4 seconds
                println("Got hit, waiting");
            }
//            else if (chatMessageEvent.getMessage().contains("For completing this event")) {
//                botState = BotState.INBETWEEN_EVENTS;
//                println("Completed Event Section");
//            }
            else if (chatMessageEvent.getMessage().contains("Very wild")) {
                botState = BotState.IDLE;
                println("Fully completed Event");
            }
        });
    }

    @Override
    public void onLoop() {
        //Loops every 100ms by default, to change:
        //this.loopDelay = 500;
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN || botState == BotState.IDLE) {
            //wait some time so we dont immediately start on login.
            Execution.delay(random.nextLong(3000,7000));
            return;
        }

        /////////////////////////////////////Botstate//////////////////////////
        switch (botState) {
            case IDLE ->                {println("We're idle!");
                Execution.delay(random.nextLong(1000,3000));}
            case EVIL_TREE_CHOPPING ->  {Execution.delay(handleChopping(player));}
            case EVIL_TREE_KINDLING ->  {Execution.delay(handleKindling(player));}
            case EVIL_TREE_NURTURE ->   {Execution.delay(handleNurture(player));}
            case INBETWEEN_EVENTS ->    {Execution.delay(handleInBetween(player));}
            case INFERNAL_STAR_MINE ->  {Execution.delay(handleMine(player));}
            case INFERNAL_STAR_PYREFIENDS ->  {Execution.delay(handlePyre(player));}
        }
    }

    private long handleKindling(LocalPlayer player) {
        /////////////////Kindling Collecting/////////////////
        Npc Kindling = NpcQuery.newQuery().name("Fire spirit").results().first();
        if (Kindling != null) {
            println("Kindling found");
            if (Backpack.isFull()) {
                println("BackPack Full Delivering");
                SceneObject Evil_Tree = SceneObjectQuery.newQuery().id(124775).results().first();
                Execution.delay(random.nextLong(500, 1000));
                Evil_Tree.interact("Burn kindling");
                println("Depositing kindling");
                return random.nextLong(250, 500);
            } else if (player.getAnimationId() == 23314 && !player.isMoving()) {
                println("Already Collecting");
                return random.nextLong(300, 500);
            } else if (player.getAnimationId() == -1) {
                Execution.delay(random.nextLong(300, 500));
                Kindling.interact("Harvest kindling");
                println("Getting kindling");
            } else if (Kindling == null){
                SceneObject Evil_Tree = SceneObjectQuery.newQuery().id(124775).results().first();
                Execution.delay(random.nextLong(500, 1000));
                Evil_Tree.interact("Burn kindling");
                println("No kindling, depositing then switching to inbetween events");
                botState = BotState.INBETWEEN_EVENTS;
            }
        }
        return random.nextLong(300, 500);
    }


    private long handleInBetween(LocalPlayer player) {
        long maxDuration = 300000; // 5 minutes in milliseconds
        Callable<Boolean> condition = () -> {
            // Check if the bot is already idle at the beginning
            if (botState == BotState.IDLE) {
                return true; // Predicate returns true, ending the delay
            }

            println("Checking for Event");
            Execution.delay(random.nextLong(1000,3000)); // Delay for 1 to 3 seconds
            Npc Kindling = NpcQuery.newQuery().name("Fire spirit").results().first();
            if (Kindling != null) {
                botState = BotState.EVIL_TREE_KINDLING;
                return true; // Predicate returns true, ending the delay
            }

            SceneObject Evil_Tree = SceneObjectQuery.newQuery().id(124773).results().first();
            if (Evil_Tree != null) {
                botState = BotState.EVIL_TREE_NURTURE;
                return true; // Predicate returns true, ending the delay
            }

            SceneObject Evil_Tree_Chop = SceneObjectQuery.newQuery().id(124774).results().first();
            if (Evil_Tree_Chop != null) {
                botState = BotState.EVIL_TREE_CHOPPING;
                return true; // Predicate returns true, ending the delay
            }

            SceneObject Mine = SceneObjectQuery.newQuery().option("Mine").results().first();
            if (Mine != null) {
                botState = BotState.INFERNAL_STAR_MINE;
                return true; // Predicate returns true, ending the delay
            }

            Npc Pyre = NpcQuery.newQuery().name("Pyrefiend").results().first();
            if (Pyre != null) {
                botState = BotState.INFERNAL_STAR_PYREFIENDS;
                return true; // Predicate returns true, ending the delay
            }


            return false; // Continue waiting
        };

        boolean actionFound = Execution.delayUntil(maxDuration, condition);

        if (!actionFound) {
            botState = BotState.IDLE; // If no action is found after the specified duration, set the state to IDLE
            println("No Event Found");
        }

        return random.nextLong(1500, 3000); // Return a random delay
    }
    private long handleNurture(LocalPlayer player) {
        if (Backpack.isFull()) {
            // Query for bones in the backpack
            Item bones = InventoryItemQuery.newQuery(93).ids(53935).results().first();

            if (bones != null) {
                // Interact with the bones to grind them
                Execution.delay(random.nextLong(500, 1000));
                Backpack.interact(bones.getName(), "Grind");
                println("Grinding Bones");

                // Additional logic for nurturing the Evil Tree
                SceneObject Evil_Tree = SceneObjectQuery.newQuery().id(124773).results().first();
                if (Evil_Tree != null) {
                    Execution.delay(random.nextLong(500, 1000));
                    Evil_Tree.interact("Nurture");
                    println("Nurturing Evil Tree");
                }
                return random.nextLong(250, 1500);
            }
            return random.nextLong(250, 1500);
        }
        // Then check the player's animation state
        if (player.getAnimationId() == 21192) {
            Execution.delay(random.nextLong(1000, 3000));
            println("Already Collecting");
            return random.nextLong(1500, 3000);
        } else if (player.getAnimationId() == -1) {
            SceneObject Bones = SceneObjectQuery.newQuery().option("Take bones").results().first();
            if (Bones != null) {
                Execution.delay(random.nextLong(500, 1000));
                Bones.interact("Take bones");
                println("Collecting");
            } else {
            println("Can't collect, switching to inbetween events");
            botState = BotState.INBETWEEN_EVENTS;
        }
            return random.nextLong(1500, 3000);
        }

        return random.nextLong(1500,3000);
    }

    private long handleChopping(LocalPlayer player) {
        SceneObject Evil_Tree = SceneObjectQuery.newQuery().id(124774).results().first();
        if (Evil_Tree != null) {
            if (player.getAnimationId() == -1) {
                Execution.delay(random.nextLong(500, 1000));
                Evil_Tree.interact("Chop");
                println("Chopping Bloodwood Tree");
            } else if (player.getAnimationId() == -21192) {
                Execution.delay(random.nextLong(1000, 3000));
                println("Already chopping");
            }
            return random.nextLong(1500, 3000);
        } else {
            println("Can't chop, switching to inbetween events");
            botState = BotState.INBETWEEN_EVENTS;
        }
        return random.nextLong(1500, 3000);
    }

    //////////////////////////////////////////Infernal Star//////////////////////////
    ////Check If Food is needed
    public boolean shouldEat(LocalPlayer player) {
        double healthPercentage = ((double) player.getCurrentHealth() / player.getMaximumHealth()) * 100;
        return healthPercentage < 40;
    }

    ////Stage 1: Mine
    private long handleMine(LocalPlayer player) {
        SceneObject Mine = SceneObjectQuery.newQuery().id(124772).results().first();
        if (Mine != null) {
            Mine.interact("Mine");
            println("Mining");
            if (shouldEat(player)) {
                ActionBar.useAbility("Eat Food");
                println("Eating");
            }
        }
        else {
            println("Can't mine, switching to inbetween events");
            botState = BotState.INBETWEEN_EVENTS;
        }
        return random.nextLong(1500, 3000);
    }

    ////Stage 2:Pyrefiends
    private long handlePyre(LocalPlayer player) {
        Npc Pyre = NpcQuery.newQuery().name("Pyrefiend").results().first();
        if (Pyre != null) {
            if (player.getAnimationId() == -1) {
                Pyre.interact("Attack");
                println("Attack Pyre");
            }
            if (shouldEat(player)) {
                ActionBar.useAbility("Eat Food");
                println("Eating");
            }
        }
        else {
            println("Can't find any Pyre, switching to idle");
            botState = BotState.IDLE;
        }
        return random.nextLong(500, 1000);
    }





    ////////////////////Botstate/////////////////////
    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }
}
