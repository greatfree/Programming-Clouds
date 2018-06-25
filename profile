# ~/.profile: executed by the command interpreter for login shells.
# This file is not read by bash(1), if ~/.bash_profile or ~/.bash_login
# exists.
# see /usr/share/doc/bash/examples/startup-files for examples.
# the files are located in the bash-doc package.

# the default umask is set in /etc/profile; for setting the umask
# for ssh logins, install and configure the libpam-umask package.
#umask 022

# if running bash
if [ -n "$BASH_VERSION" ]; then
    # include .bashrc if it exists
    if [ -f "$HOME/.bashrc" ]; then
	. "$HOME/.bashrc"
    fi
fi

# set PATH so it includes user's private bin if it exists
if [ -d "$HOME/bin" ] ; then
    PATH="$HOME/bin:$PATH"
fi

JAVA_HOME=/opt/jdk1.8.0
ANT_HOME=/opt/ant1.10.1
PATH=$HOME/bin:$JAVA_HOME/bin:$ANT_HOME/bin:/bin:$PATH:.
CLASSPATH=.
CLASSPATH=$JAVA_HOME/lib:$ANT_HOME/lib:$CLASSPATH
JAVA_OPTS="-Xms512m -Xmx1024m"
ANT_OPTS="-Xms512m -Xmx1024m"

export PATH JAVA_HOME ANT_HOME CLASSPATH JAVA_OPTS ANT_OPTS
