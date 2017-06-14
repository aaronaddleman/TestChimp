# TestChimp

## overview

This project is for showing a simple form that can be built with clojurescript. It has three fields which have effects when the come into focus and blur. The last field of a password will list 3 requirements necessary. As the password is filled out and meets a requirement, the condition will disappear from the list.

## starting the application

### spacemacs

Open up the project.clj, then:

```
SPC m s i
```

switch to the cider-repl

```
SPC b b
```

start the load the figwheel-sidecar.repl-api, start figwheel, and cljs-repl by typing the following into the cider:

```
(do (use 'figwheel-sidecar.repl-api) (start-figwheel!) (cljs-repl))
```

open your browser to http://localhost:3449, you should see the prompt return when the page loads, then use figwheels autobuild

```
(start-autobuild)
```

this allows you to make changes to your code and not have to reload the web page

### from another editor and the terminal

Open up the project.clj, then in a terminal at the base dir of the project:

```
lein run server
```

open a new terminal at the base dir of the project:

```
lein figwheel
```

## making changes

Looking at the project.clj you should notice the line:

```
:figwheel {:on-jsload "angular-phonecat-re-frame.core/mount-root"}
```

This line says it will load the namespace of `angular-phonecat-re-frame.core` and execute the function `mount-root`. It is at the function of `mount-root` where the application starts to take place. This namespace is located in the file of `src/cljs/angular\_phonecat\_re\_frame/core.cljs`.
