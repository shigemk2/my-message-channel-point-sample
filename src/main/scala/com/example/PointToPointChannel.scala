package com.example

import akka.actor._

class CompletableApp(val steps:Int) extends App {
  val canComplete = new java.util.concurrent.CountDownLatch(1)
  val canStart = new java.util.concurrent.CountDownLatch(1)
  val completion = new java.util.concurrent.CountDownLatch(steps)

  val system = ActorSystem("eaipatterns")

  def awaitCanCompleteNow = canComplete.await
  def awaitCanStartNow = canStart.await
  def awaitCompletion = {
    completion.await
    system.shutdown
  }
  def canCompleteNow() = canComplete.countDown()
  def canStartNow() = canStart.countDown()

  def completeAll() = {
    while (completion.getCount > 0) {
      completion.countDown()
    }
  }

  def completeStep() = completion.countDown()
}

object PointToPointChannelDriver extends CompletableApp(4) {
  val actorB = system.actorOf(Props[ActorB])

  actorB ! "Goodbye, from actor C!"
  actorB ! "Hello, from actor A!"
  actorB ! "Goodbye again, from actor C!"
  actorB ! "Hello again, from ActorA!"

  // awaitCompletion

  Thread.sleep(1000)
  system.terminate
  println("PointToPointChannel: completed.")
  // system.terminate()
}

class ActorB extends Actor {
  var goodbye = 0
  var goodbyAgain = 0
  var hello = 0
  var helloAgain = 0

  def receive = {
    case message: String =>
      hello = hello +
        (if (message.contains("Hello")) 1 else 0)
      helloAgain = helloAgain +
        (if (message.startsWith("Hello again")) 1 else 0)
      assert(hello == 0 || hello > helloAgain)

      goodbye = goodbye +
        (if (message.contains("Goodbye")) 1 else 0)
      goodbyAgain = goodbyAgain +
        (if (message.startsWith("Goodbye again")) 1 else 0)
      assert(goodbye == 0 || goodbye > goodbyAgain)

      println("--------------------")
      println(s"goodbye: $goodbye")
      println(s"goodbyeAgain: $goodbyAgain")
      println(s"hello: $hello")
      println(s"helloAgain: $helloAgain")
      println("--------------------")
      PointToPointChannelDriver.completeStep()
  }
}
