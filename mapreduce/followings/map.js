function(doc, meta) {
  if(doc.type == "FOLLOWING") {
    emit(doc.id1, [doc.id2]);
  }
}
