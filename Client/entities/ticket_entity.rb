# Entidade que representa a base de dados das passagens para o lado do cliente e da agência.
class TicketEntity
    attr_reader :id
    attr_reader :ticket_type, :origin, :destination
    attr_reader :departure_date, :return_date
    attr_reader :num_people, :price

    TYPES = ["ida e volta", "somente ida"]

    def initialize(ticket_id: nil, ticket_type:, origin:, destination:,
        departure_date:, return_date:, num_people:,price:)
        @id = ticket_id
        @ticket_type = ticket_type
        @origin = origin
        @destination = destination
        @departure_date = departure_date
        @return_date = return_date
        @num_people = num_people
        @price = price
    end

    def json_converter
        hash = {}

        hash['ticketType'] = @ticket_type
        hash['origin'] = @origin
        hash['destination'] = @destination
        hash['departureDate'] = @departure_date
        hash['returnDate'] = @return_date if @return_date
        hash['numPeople'] = @num_people
        hash['price'] = @price

        return hash.to_json
    end

    def string_converter
        str = ""

        str += "-----------------------------------\n"
        str += "Oferta de passagem aérea \##{@id}\n"
        str += "  Tipo: #{@ticket_type}\n"
        str += "  Origem: #{@origin}\n"
        str += "  Destino: #{@destination}\n"
        str += "  Para: #{@num_people} pessoas\n"
        str += "  Data de partida: #{@departure_date.strftime('%d/%m/%Y')}\n"
        str += "  Data de retorno: #{@return_date.strftime('%d/%m/%Y')}\n" if @return_date
        str += "  Preço: #{@price}\n"
        str += "-----------------------------------\n"

        return str
    end

end
