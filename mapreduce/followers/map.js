function(doc, meta) {
  if(doc.type == "FOLLOWER") {
    emit(doc.id1, [doc.id2]);
  }
}