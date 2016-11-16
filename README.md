# my-message-channel-point-sample

メッセージ送信者とメッセージ受信者が1対1の関係になっている非常にシンプルなパターン。

CompletableAppはCoundDownLatch.awaitとsystem.terminateを使っている。

FIFOなメールボックス。

https://github.com/VaughnVernon/ReactiveMessagingPatterns_ActorModel

```sh
activator run
```
