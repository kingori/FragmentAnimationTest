# FragmentAnimationTest

This is a sample project to reproduce [fragment animation issue](https://issuetracker.google.com/issues/37036000)

## How to reproduce bug
1. Start application
2. Press "Show Sub" button
3. Press "Finish & Change Fragment" button
4. Press "Finish me!" button
5. You'll be brought back to MainActivity. If you press "Show A" or "Show B" button, fragment is changed but view is not changing.

## How to avoid this bug
1. Strat application
2. Press "FragmentHostLayout" . In this activity, a custom view(FragmentHostLayout) is used.
3. follow "reproduce bug" steps 2~4
4. You'll be brought back to MainActivity2. In this case, "Show A" or "Show B" button works normally.
