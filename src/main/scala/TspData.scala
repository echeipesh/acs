/**
 * User: eugene
 * Date: 12/12/13
 */
case class Point(id:String, x:Double, y:Double){
  def distanceTo(p: Point):Double = math.sqrt(math.pow(x-p.x, 2) + math.pow(y-p.y, 2))
}

object TspData {
  def readTspFile(name: String):Array[Array[Double]] = {
    val source = io.Source.fromURL(getClass.getResource("/" + name))
    val points = (
      for {
        line <- source.getLines()
        Array(id, x, y) = line.split(" ")
      } yield
        Point(id, x.toDouble, y.toDouble)
    ).toArray

    for {p1 <- points} yield
      for {p2 <- points} yield
        p1.distanceTo(p2)
  }
}
