try {
  require("./all.test");
  console.log("Node gateway tests passed:");
} catch (error) {
  console.error(error);
  process.exit(1);
}
