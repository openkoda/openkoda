# Contribution Guidelines

## Commit Message

The expected structure for git commit messages is:

```
 [<feature short name>] <Change subject>

Commit message body.
This section explains how things worked before this commit, what has changed, and how things work now.

```

* Start with a unique feature/fix short name, preferable matching with a branch originally worked on
* Keep the commit subject text brief, 72 chars max
* Start subject with a capital letter
* No period at the end of subject line
* Use imperative statements in the subject line ("Fix", "Add", "Change" instead of "Fixed", "Added", "Changed")
* Add an empty line after the subject
* Add line breaks to the description section to make it more readable


## Source Code Management
When working on a new release
0.  Create a fork
1.  Create a feature branch from ``main``, for example `fix_for_xxx`
    When developing features for specific release branch, create separate feature branches for example `1.5_my_feature`
2.  Optionally set main ``<version></version>`` of Openkoda core to next SNAPSHOT minor version, for example 1.5.0-SNAPSHOT
    *Since it's all in your private fork it's up to you how to deal with SNAPSHOT and versions*
4.  Create a PR to upstream ``main`` once changes are released. Add overall scope of changes in PR's description
    *Version in ``pom.xml`` should **NOT** be SNAPSHOT., for example 1.5.0*
