# screeps-cljs

Basic framework for getting started in Screeps using ClojureScript.

Run script/watch for live compilation. Add your username and password (not to the repository!!) for instant deployment.

The game loop is tied up in main.cljs. Memory is implemented using transit-cljs, because it’s faster and more importantly it _works_.

The existence of the jsx->clj function saddens me, but I haven’t been able to work out why js->clj doesn’t work on world. If you figure it out and fix it, I will merge your pull request _so fast_.

Enjoy the thrills of incomprehensible error messages because it’s all minified and the function names are meaningless. Savor the perverse joy you feel when you can actually read what the JS is doing anyway. Curse in frustration when your carefully tested and debugged in the simulator code crashes and burns on world and you have no choice but to hang up your entire AI trying to debug what could possibly have gone wrong.

You’re gonna type (.log js/Console) a lot, is what I’m saying.

Happy hacking!
