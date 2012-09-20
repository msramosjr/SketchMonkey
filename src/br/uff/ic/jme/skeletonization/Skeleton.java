package br.uff.ic.jme.skeletonization;

import java.util.ArrayList;
import java.util.List;

public class Skeleton
{
   public List<SkeletonBranch> branches;
   
   public Skeleton()
   {
      branches = new ArrayList<SkeletonBranch>();
   }
}
