function(key, values, rereduce) {
  if(rereduce) {
    var result = [];
    for(var i = 0; i < values.length; i++) {
      result += values[i];
    }
    return result;
  }
  else {
    return values;
  }
}
