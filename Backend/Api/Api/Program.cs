using System.Text;
using Api.Database;
using Api.Interfaces;
using Api.Services;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using MongoDB.Driver;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.Configure<DatabaseConnection>(
    builder.Configuration.GetSection("DatabaseSettings"));

builder.Services.AddSingleton<IDatabaseConnection>(sp =>
    sp.GetRequiredService<IOptions<DatabaseConnection>>().Value);

builder.Services.AddSingleton<IMongoClient>(s =>
    new MongoClient(builder.Configuration.GetValue<string>("DatabaseSettings:ConnectionString")));

builder.Services.AddScoped<IUserService, UserService>();




//Add Authentication
builder.Services.AddAuthentication(
    JwtBearerDefaults.AuthenticationScheme).AddJwtBearer(options => {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuerSigningKey = true,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(builder.Configuration.GetSection("AppSettings:JwtToken").Value)),
            ValidateIssuer = false,
            ValidateAudience = false
        };

    });



builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseAuthorization();

//Add Authentication
app.UseAuthentication();

app.MapControllers();

app.Run();
