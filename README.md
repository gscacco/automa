automa
======

Automa is a Java library to easily create and use Finite State Machines.

Defining a finite state machine is a simple as defining plain enums for the states and the events like:

    public enum PlayerState {
        STOP,
        PLAY,
        PAUSE
    }

    public enum PlayerEvent {
       STOP,
       PLAY,
       PAUSE
    }

Then you can simply create a Finite State Machine for a Player by doing:

    Automa<PlayerState, PlayerEvent> player = new Automa<PlayerState, PlayerEvent>(PlayerState.STOP);
    player.from(PlayerState.STOP).goTo(PlayerState.PLAY).when(PlayerEvent.PLAY).andDo(new Runnable() {
        public void run() {
            // TODO: Play music
        }
    });
    player.from(PlayerState.PLAY).goTo(PlayerState.STOP).when(PlayerEvent.STOP).andDo(new Runnable() {
        public void run() {
            // TODO: Stop music
        }
    });
    player.from(PlayerState.PLAY).goTo(PlayerState.PAUSE).when(PlayerEvent.PAUSE).andDo(new Runnable() {
        public void run() {
            // TODO: Pause music
        }
    });
    player.from(PlayerState.PAUSE).goTo(PlayerState.PLAY).when(PlayerEvent.PLAY).andDo(new Runnable() {
        public void run() {
            // TODO: Resume music
        }
    });
    player.from(PlayerState.PAUSE).goTo(PlayerState.STOP).when(PlayerEvent.STOP).andDo(new Runnable() {
        public void run() {
            // TODO: Stop music
        }
    });

The last step will simply connect your event source to `player` to send it the events to handle:

    if (userPressedPlay())       player.signalEvent(PlayerEvent.PLAY);
    else if (userPressedStop())  player.signalEvent(PlayerEvent.STOP);
    else if (userPressedPause()) player.signalEvent(PlayerEvent.PAUSE);
