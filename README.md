# robogenesis-indigo

This is a port of my Ludum Dare 28 jam game (Compo) entry -- Robogenesis -- to [Indigo](https://indigoengine.io/).

WARNING: this is a learning project for me, so don't assume this code represents Indigo best-practices or anything.

Robogenesis was originally written for LibGDX. That version can be seen here: https://github.com/JavadocMD/robogenesis-gdx

The old Ludum Dare site is no longer available, but someone was kind enough to save a copy of the [entry page](https://ludumdata.openfu.com/game/22193).

## Development

Environment: Linux with `mill` and the Node module `http-server` globally installed.

To test in a browser:

```bash
mill game.buildGame
./serve.sh
```

Then visit http://localhost:8080
