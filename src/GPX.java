/*

  Copyright (c) 2006-2011 Tom Carden

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA

*/

// <gpx>
//   <wpt>
//   </wpt>
//   <!-- more wpts -->
//   <trk>
//     <name>whatever</name>
//     <trkseg>
//       <trkpt lat="" lon="">
//         <ele></ele>
//         <time>yyyy-MM-ddThh:mm:ssZ</time>
//       </trkpt>
//       <!-- more trkpts -->
//     </trkseg>
//     <!-- more trksegs -->
//   </trk>
//   <!-- more trks -->
// </gpx>

import processing.core.PApplet;
import processing.data.XML;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Vector;

/** contains a collection of GPXTrack objects and GPXWayPoint objects */
public class GPX {

  public static boolean debug = false;

  PApplet parent;

  private Vector wayPoints = new Vector();
  private Vector tracks = new Vector();

  public GPX(PApplet parent) {
    this.parent = parent;
  }

  public void parse(String url) {
    try {
      XML xmldata = parent.loadXML(url);
      for (XML xmlthing : xmldata.getChildren()) {
        if (xmlthing.getName().equals("trk")) {
          addTrack(new GPXTrack(xmlthing));
        }
        else if (xmlthing.getName().equals("wpt")) {
          addWayPoint(new GPXWayPoint(xmlthing));
        }
      }
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /* Tracks */

  public void addTrack(GPXTrack trk) {
    tracks.addElement(trk);
  }

  public void removeTrack(GPXTrack trk) {
    tracks.removeElement(trk);
  }

  public GPXTrack getTrack(int i) {
    return (GPXTrack)tracks.elementAt(i);
  }

  public int getTrackCount() {
    return tracks.size();
  }

  /* WayPoints */

  public void addWayPoint(GPXWayPoint wpt) {
    wayPoints.addElement(wpt);
  }

  public void removeWayPoint(GPXWayPoint wpt) {
    wayPoints.removeElement(wpt);
  }

  public GPXWayPoint getWayPoint(int i) {
    return (GPXWayPoint)wayPoints.elementAt(i);
  }

  public int getWayPointCount() {
    return wayPoints.size();
  }

  /* Testing */

  public static void main(String[] args) {
    if (args.length >= 1) {
      GPX gpx = new GPX(new PApplet());
      GPX.debug = true;
      System.out.print("parsing " + args[0] + "... ");
      gpx.parse(args[0]);
      System.out.println("done.");
      System.out.println(gpx.getTrackCount() + " tracks parsed:");
      StringBuilder sb = new StringBuilder();
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      for (int i = 0; i < gpx.getTrackCount(); i++) {
        GPXTrack trk = gpx.getTrack(i);
        System.out.println("Track " + i + " has " + trk.size() + " track segments");
        for (int j = 0; j < trk.size(); j++) {
          GPXTrackSeg trkseg = trk.getTrackSeg(j);
          System.out.println("Segment (" + i + "," + j + ") has " + trkseg.size() + " track points");
          for (int k = 0; k < trkseg.size(); k++) {
            GPXPoint pt = trkseg.getPoint(k);
            System.out.println(simpleDateFormat.format(pt.time) +" "+ pt.lat + "," + pt.lon );
            sb.append(simpleDateFormat.format(pt.time) + "," + pt.lat + "," + pt.lon + "\n");
          }
        }
      }
      save_file(sb.toString());
    }
    else {
      System.err.println("no filename supplied");
    }
  }

  public static void save_file(String data) {
    File file = new File("gps_data.txt");

    try {
      //파일에 문자열을 쓴다.
      //하지만 이미 파일이 존재하면 모든 내용을 삭제하고 그위에 덮어쓴다
      //파일이 손산될 우려가 있다.
      FileWriter fw = new FileWriter(file);
      fw.write(data);
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

