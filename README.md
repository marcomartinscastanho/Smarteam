# Smarteam #
Android app to manage Sunday league games with your friends

## Table of Contents ##
1. [Motivation](#motivation)
2. [Objectives and Structure](#objectives-and-structure)
3. [What's left?](#whats-left)
4. [Future ideas](#future-ideas)

## Motivation ##
A while ago I used to play 5-a-side football weekly with my college friends. Every week we had the same problem: we had no established teams, so someone had to decide who faced who. No one wanted to have the task of drawing their friends into teams because in the end everyone would complain that the teams were unbalanced and the game was not even. This really bothered me since it would delay the start of the game and make for games to often be very one-sided.

In order to solve this, I came up with an idea for automating the teams drawing process and making teams even. The idea was to set a score to every player, based on their win percentage so far (I started recording the results every week), and allocating players into teams making sure the total score of both teams would be as close as possible. Also, I would publish the players ranking on our facebook group after every game, so everyone knew how good or bad they were performing.

Over time, I adjusted the score formula in order to get a more reliable estimate of a players recent form and its expected performance, which consisted in attributing points to results (ex: 3 per win, 1 for draw, 0 for absence, -2 for defeat) and making a weighted average giving a higher weight to latest results.

Eventually, our weekly games became very even and competitive, since no one wanted to see their names on the bottom half of the weekly ranking. My friends who initially joked on my method now praised it for making games more interesting.

The first version on this tool was made in Excel, in a very user-unfriendly way that only I could use. When I start playing with my IT-work colleagues, I thought about using the tool here too, but I could not show them such an ugly thing. Therefore I decided to migrate it to a more presentable platform. I had been thinking about learning Android Development for a while, and I decided to learn on the job while re-factoring the teams allocation tool.

I made a first version, a very amateur and low performing one, with data stored in text files since I didn't know Android supported SQL Lite. Later I discarded this version and started a new one from scratch, this time trying to make it cleaner and faster and more ambitious.

I really enjoy developing for Android and while I was developing this App I thaught I could make this available on Play Store. Even if no one would ever used, at least I could show it on my cv. But working 8h a day as an IT developer and go home to work a few more hours on Android is exhausting and social life-consuming, so the progress of this App stalled, as you can see on the commits history.

First I had all code private since I wanted to do this all by myself, I was very protective of my baby and didn't want any external influence on this. But I realized that it's better to allow someone else to contribute to finish the App than to keep it locked in my computer and never finishing it.


So, if you're interested in contribute to finish the development of this Android App, here are some ground ideas and design I would like to keep.

## Objectives and Structure ##
As explained before, the objective of this App is to keep track of the performance of every **Player** in a group of friends you play with (here I call it a **Team**), generating balanced teams for future games based on the Score of each participating player and keeping a Ranking of the Team Players.

The **Main Menu** Activity has 4 buttons:
* New
* Load
* Delete
* Settings

**New**, **Load** and **Delete** here refers to Teams, in case you have regular games with more than a group of friends (ex: former college friends; current work colleagues). Both this 3 buttons open a dialog in which the user can set a name to a new Team or select an existing one to Load or Delete. The **Settings** button goes to a **Settings Menu**, in which the user can set different values to Wins, Draws, Defeats, etc. or restore the default ones.

After creating a New Team or Loading an existing one, a different Acitivity is opened. I called this the **Team Menu** Activity. This activity has 5 buttons:
* Results
* Ranking
* Lineups
* Statistics
* Edit

Let's start from the top:
The Results button opens another a **Team->Results Menu** Activity which allows to manage the games results. It has 4 buttons:
* Add, to add a new result
* View, to view all results
* Edit, to edit a result (I haven't decided yet if I will keep this)
* Delete, to delete a result (same as before)

The **Ranking** button just shows an Activity with the ranking of the Team's players, with each line of the table containing the Player's position, its name and its Score. This Activity has a Share Button on the Action Bar, allowing the user to share a snapshot of the Ranking on several platforms such as Facebook, Whatsapp, Twitter or e-mail.

The **Lineups** button is where the magic happens. It shows a dialog asking the user to select the players who will play on the next game, upon confirmation, opens a new activity showing the allocation of those players into 2 groups with average scores the closest possible. Like in the Ranking activity, this is also shareable.

The **Statistics** button opens a **Statistics Menu** Activity showing statistics that I haven't quite decided about. It will be something like "Player with most games", "Player with most win", "Player with most defeats in a row", etc. Also shareable.

The **Edit** button opens an **Edit Menu** Activity that allows the user to manage the Team. It has 4 buttons:
* Rename Team
* Add Player
* Rename Player
* Delete Player

All are self-explanatory


## What's left? ##
* The basic functions are mostly working properly, except for some likely not yet identified bugs;
* As mentioned, the Statistics Menu is not done yet, not even started. I have done however a set of methods with queries that may be useful.
* I thaught about replacing the structure of the Results Menu with a Card List, with each game result (the date, result and players involved) presented in a card, and the options to Edit or Delete a result being prompted upon tapping on the corresponding card. The Add button would be a floating round button. I actually begin implementing this idea, but it is only half done and not working, so I haven't commited it yet.

I have a few more ideas for this, which are listed below, but right know I just want to finish a first fully working version of this App.


## Future ideas ##

### Smarteam 1 ###
* No authentication
* No Sync
* Data stored only locally
* Only English

#### Free version ####
* Has adds
* * banner adds
* * full screen add when generating line-up
* Limited to 5x5 line-ups
* Limited to 15 players


### Smarteam 2 - Future version ###
* Authentication
* Every team is synched among its user members
* Data is stored in a cloud, in order to be accessed by all team members
