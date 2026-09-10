Yes — **I think this is a good small F-Droid project to build**, especially if you keep it privacy-friendly and focused.

I searched for existing Android/F-Droid projects. There are several open-source Rock Paper Scissors Android implementations, including ones with computer play, but I **didn't find a clear F-Droid app matching your specific idea**. For example, there are GitHub Android apps for playing against the computer, such as this one, but they don't appear to provide the exact local two-player "hide Player 1's choice, then hand the phone to Player 2" experience you're describing. ([GitHub][1])

### Your idea is actually quite nice

I'd make it a **tiny, completely offline game**:

![Image](https://images.openai.com/static-rsc-4/rSAfJtw5Yk4-eES_z1vYqcOCOxGdTRt6vYoITXm-017S2HPK3Ov3pJqjwcqbyNg5TOEdmXFeuAm7ehDIvIOyCNjMPD5FGdsqXrF7mL0f5jzz07Vn6GNou4za8QmHVuDY95zRsgPs--g2TMcWGVOlbq-tT2Mu0eO8yCvHhRDHZcEdifwjN5HLZgu9KJ-ciyBN?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/5cMJY4Ul8LkBlFNTS5t92E5ZfnhyFdvCY74UgKkxRYoKE_shpwGVNYGJAuAALhFF7l7gXr9maWjcU8J0OFajfybHkNUEGJpoumheaGGxWc9Wc2gy8XTpx07Jsg65hh5OZsYrRhXqW288ZcOFmCj-9WokaCKb8RJVvaG9Xxj1a9n7yP3G04VZTt7spzmcojqW?purpose=fullsize)

![Image](https://images.openai.com/static-rsc-4/pv78Ca7BoPrQ0Agb6v3WjUuytj-UgsGLCAUBDRBXSnTJ5utjId4ozupWP7x2r7Hvr2z3rGfuWeyw4zmLf05JvGFeiG_N1vdj2tKsiB-J_UgGGYqBb7kkeN2aWwHfTgX1UtFb91A3hX_eKbdyIQRgtcrHi8mxAJiPcPqLd0Q8TZNlI5JRV5FS7pPS3kzCMsoc?purpose=fullsize)

**Modes:**

1. **🤖 vs Computer**

   * Choose Rock / Paper / Scissors
   * Bot chooses randomly
   * Show both choices
   * Scoreboard
   * Best-of-3 / Best-of-5 / unlimited

2. **👥 Two Players — Pass & Play**

   * Player 1 chooses
   * Choice is immediately hidden
   * Screen says **"Pass the phone to Player 2"**
   * Player 2 chooses
   * Reveal both choices
   * Announce winner
   * Next round

The second mode is the interesting differentiator.

### I'd make the two-player flow particularly secure

Something like:

**Player 1**

> Player 1
> Choose your move
>
> ✊ Rock   ✋ Paper   ✌️ Scissors

After tapping:

> ✓ Choice locked
>
> **Pass the phone to Player 2**
>
> [Continue]

Then:

> Player 2
> Choose your move

After Player 2 chooses:

> **Reveal!**
>
> Player 1: ✊ Rock
> Player 2: ✌️ Scissors
>
> **Player 1 wins!**

This prevents Player 2 from accidentally seeing Player 1's choice.

### And there's a nice F-Droid philosophy fit

You could make it:

* **100% offline**
* **No accounts**
* **No internet permission**
* **No ads**
* **No analytics**
* **No tracking**
* **No unnecessary permissions**
* Open source
* Tiny APK
* Simple UI

That makes the app much more compelling than yet another generic RPS game.

There are already open-source RPS projects with multiplayer implementations—for example, one Android project uses Firebase for real-time multiplayer—but that's a very different proposition from your **local, pass-and-play, no-server-required** approach. ([GitHub][2])

### I would *not* add online multiplayer initially

I'd deliberately keep the first version extremely small:

**v1.0**

* Player vs Bot
* Player vs Player (same device)
* Pass-and-play hiding
* Score
* Best of 3/5/10
* Reset game
* Dark/light theme
* Offline
* No permissions
* "First to X"
* Match history
* Statistics

Then potentially later:
* Custom player names
* Rock Paper Scissors **Lizard Spock**
* Tournament mode

One particularly nice feature would be a **"quick decision" mode** where you can use it as an actual decision-making tool: *"Who gets the last slice of pizza?"* → two people pass the phone → result.

### Should you build it?

**Yes.** Not because nobody has ever made Rock Paper Scissors—the basic game obviously exists everywhere—but because you can make a **very polished, privacy-first, F-Droid-native version** with a genuinely useful local multiplayer interaction.

I'd also avoid making it overly game-y. A clean app called something like **"RPS — Rock Paper Scissors"** or **"Janken"** could be almost absurdly simple.

If you're interested, I can also help you design the **complete v1 feature set + screen-by-screen UI + Android architecture + F-Droid submission requirements**, so you could go from idea → Git repo → F-Droid fairly quickly.

[1]: https://github.com/Eyal-Davidi/rock-paper-scissors?utm_source=chatgpt.com "GitHub - Eyal-Davidi/rock-paper-scissors: Rock, Paper, Scissors game against the computer with game history for Android · GitHub"
[2]: https://github.com/honeybadger2788/RockPaperScissors?utm_source=chatgpt.com "GitHub - honeybadger2788/RockPaperScissors: La aplicación de Android es un juego de \"Piedra Papel o Tijera\" que ofrece una experiencia de juego divertida y emocionante, permitiendo a los jugadores desafiar a sus amigos en partidas multijugador en tiempo real mediante la integración con Firebase Realtime Database. · GitHub"
